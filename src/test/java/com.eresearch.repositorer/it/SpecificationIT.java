package com.eresearch.repositorer.it;

import com.eresearch.repositorer.EresearchRepositorerApplication;
import com.eresearch.repositorer.application.configuration.JmsConfiguration;
import com.eresearch.repositorer.core.FileSupport;
import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.lookup.NameLookupStatus;
import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Record;
import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordSearchResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedRecordDto;
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import com.eresearch.repositorer.repository.RecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = EresearchRepositorerApplication.class,
        properties = {"application.properties"}
)
@RunWith(SpringRunner.class)
public class SpecificationIT {


    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private DynamicExternalSystemMessagesAwaitingRepository awaitingRepository;

    private RestTemplate restTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(1234);

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }


    @Test
    public void author_extraction_works_as_expected() throws Exception {

        // given
        String transactionId = "102aaf3f-f512-4cb7-8557-ca7c4f7abec2";

        String repositorerFindDtoAsString = FileSupport.getResource("test/first_case_input.json");

        RepositorerFindDto repositorerFindDto = objectMapper.readValue(repositorerFindDtoAsString, RepositorerFindDto.class);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Transaction-Id", transactionId);

        HttpEntity<RepositorerFindDto> httpEntity = new HttpEntity<>(repositorerFindDto, httpHeaders);

        mockFirstWorkflowCommunications();

        // Note: also mock scopus connector communication when we have results from author elsevier (AUTHOR_RESULTS_QUEUE)
        stubFor(post(urlEqualTo("/scopus-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\"au-id\" : \"6507083122\" \n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));


        // when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/repositorer/extract",
                HttpMethod.POST,
                httpEntity,
                String.class);


        // then
        Assert.assertNotNull(responseEntity.getBody());

        String body = responseEntity.getBody();
        JSONAssert.assertEquals(FileSupport.getResource("test/first_case_output.json"), body, true);


        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                    NameLookup nameLookup = namesLookupRepository.findNamesLookupByTransactionIdEquals(transactionId);

                    Assert.assertEquals(repositorerFindDto.getFirstname(), nameLookup.getFirstname());
                    Assert.assertEquals(repositorerFindDto.getInitials(), nameLookup.getInitials());
                    Assert.assertEquals(repositorerFindDto.getSurname(), nameLookup.getSurname());

                    Assert.assertEquals(NameLookupStatus.PENDING, nameLookup.getNameLookupStatus());
                });


        // when
        injectSecondWorkflowCommunications();


        // then
        Author author = new Author();
        author.setFirstname("Christos");
        author.setSurname("Skourlas");

        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .untilAsserted(() -> {

                    Collection<RetrievedRecordDto> results = recordRepository.find(true, author);

                    Optional<RetrievedRecordDto> retrievedRecordDto = results.stream()
                            .filter(r -> r.getRecord().getTransactionId().equals(transactionId))
                            .findAny();

                    Assert.assertTrue(retrievedRecordDto.isPresent());
                });

        // --- check record's contents ---
        ResponseEntity<RecordSearchResultDto> allRecordsResponseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/repositorer/records/find-all",
                RecordSearchResultDto.class
        );
        Assert.assertNotNull(allRecordsResponseEntity.getBody());

        RecordSearchResultDto searchResultDto = allRecordsResponseEntity.getBody();
        Collection<RetrievedRecordDto> allRecords = searchResultDto.getRetrievedRecordDtos();

        Assert.assertTrue(allRecords.size() >= 1);

        RetrievedRecordDto retrievedRecordDto = allRecords
                .stream()
                .filter(r -> r.getFilename().contains("Skourlas"))
                .findAny()
                .orElseThrow(IllegalStateException::new);

        String filenameToFetchRecord = retrievedRecordDto.getFilename();
        RecordFilenameDto recordFilenameDto = new RecordFilenameDto(filenameToFetchRecord);

        ResponseEntity<RecordSearchResultDto> searchByFilenameResponseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/repositorer/records/find-by-filename?full-fetch=true",
                recordFilenameDto,
                RecordSearchResultDto.class);

        Collection<RetrievedRecordDto> searchResults = searchByFilenameResponseEntity.getBody().getRetrievedRecordDtos();
        Assert.assertEquals(1, searchResults.size());


        RetrievedRecordDto result = searchResults.iterator().next();

        RetrievedRecordDto expected = objectMapper.readValue(
                FileSupport.getResource("test/first_case_operation_result.json"),
                RetrievedRecordDto.class
        );

        Record resultRecord = result.getRecord();
        Record expectedRecord = expected.getRecord();

        Assert.assertEquals(expectedRecord.getTransactionId(), resultRecord.getTransactionId());

        Assert.assertEquals(expectedRecord.getFirstname(), resultRecord.getFirstname());
        Assert.assertEquals(expectedRecord.getInitials(), resultRecord.getInitials());
        Assert.assertEquals(expectedRecord.getLastname(), resultRecord.getLastname());

        Assert.assertEquals(expectedRecord.getNameVariants().size(), resultRecord.getNameVariants().size());

        Assert.assertEquals(expectedRecord.getEntries().size(), resultRecord.getEntries().size());

        // -----------------------------------

        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .untilAsserted(() -> {
                    NameLookup nameLookup = namesLookupRepository.findNamesLookupByTransactionIdEquals(transactionId);

                    Assert.assertEquals(repositorerFindDto.getFirstname(), nameLookup.getFirstname());
                    Assert.assertEquals(repositorerFindDto.getInitials(), nameLookup.getInitials());
                    Assert.assertEquals(repositorerFindDto.getSurname(), nameLookup.getSurname());

                    Assert.assertEquals(NameLookupStatus.COMPLETED, nameLookup.getNameLookupStatus());
                });

        // TODO check other datasources that are ok...


        // cleanup
        NameLookup nameLookup = namesLookupRepository.findNamesLookupByTransactionIdEquals(transactionId);
        namesLookupRepository.delete(nameLookup.getId());


        recordRepository.find(true, author)
                .stream()
                .filter(r -> r.getRecord().getTransactionId().equals(transactionId))
                .findAny()
                .ifPresent(r -> {

                    System.out.println("    >>> WILL DELETE JUST STORED RECORD...");
                    String filename = r.getFilename();

                    recordRepository.delete(filename);
                });


        // Note: just for throttling purposes.
        TimeUnit.SECONDS.sleep(10);
    }

    private void mockFirstWorkflowCommunications() {

        // DBLP
        stubFor(post(urlEqualTo("/dblp-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"firstname\":\"Christos\",\n" +
                        "\t\"initials\":\"\",\n" +
                        "\t\"surname\":\"Skourlas\"\n" +
                        "}\n", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/dblp-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"firstname\":\"C.\",\n" +
                        "\t\"initials\":\"\",\n" +
                        "\t\"surname\":\"Skourlas\"\n" +
                        "}\n", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/dblp-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"firstname\":\"C\",\n" +
                        "\t\"initials\":\"\",\n" +
                        "\t\"surname\":\"Skourlas\"\n" +
                        "}\n", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        // SCIENCE DIRECT
        stubFor(post(urlEqualTo("/sciencedirect-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "  \"firstname\":\"Christos\",\n" +
                        "  \"initials\":\"\",\n" +
                        "  \"surname\":\"Skourlas\"\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/sciencedirect-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "  \"firstname\":\"C.\",\n" +
                        "  \"initials\":\"\",\n" +
                        "  \"surname\":\"Skourlas\"\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/sciencedirect-consumer/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "  \"firstname\":\"C\",\n" +
                        "  \"initials\":\"\",\n" +
                        "  \"surname\":\"Skourlas\"\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        // ELSEVIER AUTHOR
        stubFor(post(urlEqualTo("/author-finder/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"author-name\":{\n" +
                        "\t\t\"firstname\":\"Christos\",\n" +
                        "\t\t\"initials\":\"\",\n" +
                        "\t\t\"surname\":\"Skourlas\"\n" +
                        "\t}\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/author-finder/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"author-name\":{\n" +
                        "\t\t\"firstname\":\"C.\",\n" +
                        "\t\t\"initials\":\"\",\n" +
                        "\t\t\"surname\":\"Skourlas\"\n" +
                        "\t}\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));

        stubFor(post(urlEqualTo("/author-finder/find-q"))
                .withRequestBody(new EqualToJsonPattern("{\n" +
                        "\t\"author-name\":{\n" +
                        "\t\t\"firstname\":\"C\",\n" +
                        "\t\t\"initials\":\"\",\n" +
                        "\t\t\"surname\":\"Skourlas\"\n" +
                        "\t}\n" +
                        "}", false, false))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Response will be written in queue.\"}")));
    }

    /*
        Note: mock by injecting messages from satellite services to ActiveMQ using the correct transactionId
                  so that aggregators could work correctly- do correct correlation based on transactionId.
     */
    private void injectSecondWorkflowCommunications() {

        // AUTHOR_RESULTS_QUEUE
        Arrays
                .asList(
                        FileSupport.getResource("test/author_finder_result/christos_skourlas.json"),
                        FileSupport.getResource("test/author_finder_result/c_dot_skourlas.json"),
                        FileSupport.getResource("test/author_finder_result/c_skourlas.json")
                )
                .forEach(r -> sendMessage(JmsConfiguration.AUTHOR_RESULTS_QUEUE, r));


        // SCOPUS_RESULTS_QUEUE (1 time)
        String scopusResult = FileSupport.getResource("test/scopus_result/christos_skourlas_6507083122.json");
        sendMessage(JmsConfiguration.SCOPUS_RESULTS_QUEUE, scopusResult);


        // DBLP_RESULTS_QUEUE (3 times == count(name_variants))
        String dblpResult = FileSupport.getResource("test/dblp_result/christos_skourlas.json");
        sendMessage(JmsConfiguration.DBLP_RESULTS_QUEUE, dblpResult);

        dblpResult = FileSupport.getResource("test/dblp_result/c_skourlas.json");
        sendMessage(JmsConfiguration.DBLP_RESULTS_QUEUE, dblpResult);

        dblpResult = FileSupport.getResource("test/dblp_result/c_dot_skourlas.json");
        sendMessage(JmsConfiguration.DBLP_RESULTS_QUEUE, dblpResult);


        // SCIDIR_RESULTS_QUEUE (3 times == count(name_variants))
        String scidirResult = FileSupport.getResource("test/scidir_result/christos_skourlas.json");
        sendMessage(JmsConfiguration.SCIDIR_RESULTS_QUEUE, scidirResult);

        scidirResult = FileSupport.getResource("test/scidir_result/c_skourlas.json");
        sendMessage(JmsConfiguration.SCIDIR_RESULTS_QUEUE, scidirResult);

        scidirResult = FileSupport.getResource("test/scidir_result/c_dot_skourlas.json");
        sendMessage(JmsConfiguration.SCIDIR_RESULTS_QUEUE, scidirResult);

    }

    private void sendMessage(String queueName, String message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

}
