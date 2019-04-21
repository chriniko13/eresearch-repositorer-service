# ERESEARCH REPOSITORER SERVICE #

### Description

This is the backbone service of eresearch repository platform.
It integrates with external systems, collects --> aggregates based on a correlation id --> transforms and stores them
in a document based database which in our case is MongoDB.


This service integrates with the following external systems:
* [eresearch-author-matcher](https://github.com/chriniko13/eresearch-author-matcher)
* [eresearch-dblp-consumer](https://github.com/chriniko13/eresearch-dblp-consumer)
* [eresearch-scidir-consumer](https://github.com/chriniko13/eresearch-sciencedirect-consumer)
* [eresearch-author-finder](https://github.com/chriniko13/eresearch-author-finder)
* [eresearch-scopus-consumer](https://github.com/chriniko13/eresearch-scopus-consumer)


It consists of two workflows in order to collect and store the data.
* `repositorer-first-step-workflow.xml` TODO explain better & diagram
* `repositorer-second-step-workflow.xml` TODO explain better & diagram

Also it handles errors with the following workflows:
* `repositorer-error-handling-aggregators-workflow.xml` TODO explain better & diagram
* `repositorer-error-handling-workflow.xml` TODO explain better & diagram


### Convenient UI (Admin Portal) in order to run repositorer operations
* [eresearch-repositorer-admin-portal](https://github.com/chriniko13/eresearch-repositorer-admin-portal)



### External Dependencies needed in order to run service (Other services && Infrastructure)

Build docker images for the following services (detailed info on how to build docker image can be found on README.md
of each one of the following described services):

* Dependencies
    * [eresearch-author-matcher](https://github.com/chriniko13/eresearch-author-matcher)
    * [eresearch-dblp-consumer](https://github.com/chriniko13/eresearch-dblp-consumer)
    * [eresearch-scidir-consumer](https://github.com/chriniko13/eresearch-sciencedirect-consumer)
    * [eresearch-author-finder](https://github.com/chriniko13/eresearch-author-finder)
    * [eresearch-scopus-consumer](https://github.com/chriniko13/eresearch-scopus-consumer)
    * MongoDB && ActiveMQ

* How to init dependencies
    * Execute: `docker-compose up`
    * Execute: `docker-compose down`


### Unit Tests (written in Groovy with Spock Framework)

* Execute: `mvn clean test`


### Integration Tests (run docker-compose first)

* Execute: `mvn clean verify`


### Create Docker Image
TODO


### How to run service (not dockerized)
TODO


### Example Request

* Hitting with HTTP POST the endpoint: `http://localhost:8889/repositorer/extract`

```json
{
	"firstname":"Anastasios",
	"initials":"",
	"surname":"Tsolakidis"
}
```


### Example Response

```json
{
    "message": "Extraction fired."
}
```

#### After Extraction, we hit MongoDB in order to see the contents of the just fired extraction

* Hitting with HTTP GET the endpoint: `http://localhost:8889/repositorer/records/find-all`

```json

{
    "retrievedRecordDtos": [
        {
            "filename": "RECORDAnastasios_NoValue_Tsolakidis#2019-04-18T20:25:14.619"
        }
    ]
}


```

* Hitting with HTTP GET the endpoint: `http://localhost:8889/repositorer/records/find-all?full-fetch=true`


```json
{
    "retrievedRecordDtos": [
        {
            "filename": "RECORDAnastasios_NoValue_Tsolakidis#2019-04-18T20:25:14.619",
            "record": {
                "id": "e9943aff-5810-474c-b9cf-5e0e6223ca6c",
                "transactionId": "f414a76c-0edb-4da6-b45b-e7c7d5bf6c6a",
                "firstname": "Anastasios",
                "initials": "",
                "lastname": "Tsolakidis",
                "nameVariants": [
                    {
                        "firstname": "A.",
                        "initials": "",
                        "surname": "Tsolakidis"
                    },
                    {
                        "firstname": "A",
                        "initials": "",
                        "surname": "Tsolakidis"
                    }
                ],
                "createdAt": "2019-04-18T17:25:14.619Z",
                "entries": [
                    {
                        "title": "AcademIS: an ontology for representing academic activity and collaborations within HEIs.",
                        "authors": [
                            {
                                "firstname": "Evangelia",
                                "initials": null,
                                "surname": "Triperina"
                            },
                            {
                                "firstname": "Cleo",
                                "initials": null,
                                "surname": "Sgouropoulou"
                            },
                            {
                                "firstname": "Anastasios",
                                "initials": null,
                                "surname": "Tsolakidis"
                            }
                        ],
                        "metadata": {
                            "Source": "DBLP",
                            "Dblp Author": "{\"authorName\":\"Anastasios Tsolakidis\",\"urlpt\":\"t/Tsolakidis:Anastasios\"}",
                            "Key": "conf/pci/TriperinaST13",
                            "Mdate": "2018-11-06",
                            "Publtype": null,
                            "Cdate": null,
                            "Authors": "[\"Evangelia Triperina\",\"Cleo Sgouropoulou\",\"Anastasios Tsolakidis\"]",
                            "Editors": null,
                            "Titles": "[\"AcademIS: an ontology for representing academic activity and collaborations within HEIs.\"]",
                            "Booktitles": "[\"Panhellenic Conference on Informatics\"]",
                            "Pages": "[\"264-271\"]",
                            "Years": "[\"2013\"]",
                            "Addresses": null,
                            "Journals": null,
                            "Volumes": null,
                            "Numbers": null,
                            "Months": null,
                            "Urls": "[\"db/conf/pci/pci2013.html#TriperinaST13\"]",
                            "Ees": "[\"https://doi.org/10.1145/2491845.2491884\"]",
                            "Cd roms": null,
                            "Cites": null,
                            "Publishers": null,
                            "Notes": null,
                            "Crossrefs": "[\"conf/pci/2013\"]",
                            "Isbns": null,
                            "Series": null,
                            "Schools": null,
                            "Chapters": null,
                            "Publnrs": null
                        }
                    },
                    {
                        "title": "PP-297. Factors that can affect birth type 10<ce:hsp sp=0.25></ce:hsp>years study",
                        "authors": [
                            {
                                "firstname": "Dimitrios",
                                "initials": null,
                                "surname": "Papadimitriou"
                            },
                            {
                                "firstname": "Athanasios",
                                "initials": null,
                                "surname": "Tsolakidis"
                            },
                            {
                                "firstname": "Hacer",
                                "initials": null,
                                "surname": "Hasan"
                            },
                            {
                                "firstname": "Anna",
                                "initials": null,
                                "surname": "Pantazi"
                            },
                            {
                                "firstname": "Eleni",
                                "initials": null,
                                "surname": "Karanikolaou"
                            }
                        ],
                        "metadata": {
                            "Source": "Science Direct",
                            "Links": "[\"https://api.elsevier.com/content/article/pii/S0378378210005906\",\"https://www.sciencedirect.com/science/article/pii/S0378378210005906?dgcid=api_sd_search-api-endpoint\"]",
                            "Load Date": "2010-11-06T00:00:00Z",
                            "Prism Url": "https://api.elsevier.com/content/article/pii/S0378378210005906",
                            "Dc Identifier": "DOI:10.1016/j.earlhumdev.2010.09.353",
                            "Open Access": "false",
                            "Open Access Flag": null,
                            "Dc Title": "PP-297. Factors that can affect birth type 10<ce:hsp sp=0.25></ce:hsp>years study",
                            "Prism Publication Name": "Early Human Development",
                            "Prism Isbn": null,
                            "Prism Issn": null,
                            "Prism Volume": "86",
                            "Prism Issue Identifier": null,
                            "Prism Issue Name": null,
                            "Prism Edition": null,
                            "Prism Starting Page": "s134",
                            "Prism Ending Page": "s135",
                            "Prism Cover Date": "2010-11-30",
                            "Prism Cover Display Date": null,
                            "Dc Creator": "Anna Pantazi",
                            "Authors": "[{\"$\":\"Anna Pantazi\"},{\"$\":\"Hacer Hasan\"},{\"$\":\"Dimitrios Papadimitriou\"},{\"$\":\"Eleni Karanikolaou\"},{\"$\":\"Athanasios Tsolakidis\"}]",
                            "Prism Doi": "10.1016/j.earlhumdev.2010.09.353",
                            "Pii": "S0378378210005906",
                            "Pubtype": null,
                            "Prism Teaser": null,
                            "Dc Description": null,
                            "Author Keywords": null,
                            "Prism Aggregation Type": null,
                            "Prism Copyright": null,
                            "Scopus Id": null,
                            "Eid": null,
                            "Scopus Eid": null,
                            "Pubmed Id": null,
                            "Open Access Article": null,
                            "Open Archive Article": null,
                            "Open Access User License": null
                        }
                    },
                    
                    .....
```


* Hitting with HTTP POST the endpoint: `http://localhost:8889/repositorer/records/find-by-filename?full-fetch=true`

    * Payload:
    ```json
      {
        "filename":"RECORDGrammati_NoValue_Pantziou#2019-04-18T21:44:50.218"
      }
    
    ```
    
    * Result:
    ```json
      {
          "retrievedRecordDtos": [
              {
                  "filename": "RECORDGrammati_NoValue_Pantziou#2019-04-18T21:44:50.218",
                  "record": {
                      "id": "7df75fa8-b063-439f-bbf4-714909a346a5",
                      "transactionId": "bd09a46a-9108-4102-8ef4-689ab53714a1",
                      "firstname": "Grammati",
                      "initials": "",
                      "lastname": "Pantziou",
                      "nameVariants": [
                          {
                              "firstname": "G.",
                              "initials": "",
                              "surname": "Pantziou"
                          },
                          {
                              "firstname": "G",
                              "initials": "",
                              "surname": "Pantziou"
                          }
                      ],
                      "createdAt": "2019-04-18T18:44:50.218Z",
                      "entries": [
                          {
                              "title": "Clustering in mobile ad hoc networks through neighborhood stability-based mobility prediction",
                              "authors": [
                                  {
                                      "firstname": "Charalampos",
                                      "initials": null,
                                      "surname": "Konstantopoulos"
                                  },
                                  {
                                      "firstname": "Grammati",
                                      "initials": null,
                                      "surname": "Pantziou"
                                  },
                                  {
                                      "firstname": "Damianos",
                                      "initials": null,
                                      "surname": "Gavalas"
                                  }
                              ],
                              "metadata": {
                                  "Source": "Science Direct",
                                  "Links": "[\"https://api.elsevier.com/content/article/pii/S1389128608000923\",\"https://www.sciencedirect.com/science/article/pii/S1389128608000923?dgcid=api_sd_search-api-endpoint\"]",
                                  "Load Date": "2008-03-16T00:00:00Z",
                                  "Prism Url": "https://api.elsevier.com/content/article/pii/S1389128608000923",
                                  "Dc Identifier": "DOI:10.1016/j.comnet.2008.01.018",
                                  "Open Access": "false",
                                  "Open Access Flag": null,
                                  "Dc Title": "Clustering in mobile ad hoc networks through neighborhood stability-based mobility prediction",
                                  "Prism Publication Name": "Computer Networks",
                                  "Prism Isbn": null,
                                  "Prism Issn": null,
                                  "Prism Volume": "52",
                                  "Prism Issue Identifier": null,
                                  "Prism Issue Name": null,
                                  "Prism Edition": null,
                                  "Prism Starting Page": "1797",
                                  "Prism Ending Page": "1824",
                                  "Prism Cover Date": "2008-06-26",
                                  "Prism Cover Display Date": null,
                                  "Dc Creator": "Charalampos Konstantopoulos",
                                  "Authors": "[{\"$\":\"Charalampos Konstantopoulos\"},{\"$\":\"Damianos Gavalas\"},{\"$\":\"Grammati Pantziou\"}]",
                                  "Prism Doi": "10.1016/j.comnet.2008.01.018",
                                  "Pii": "S1389128608000923",
                                  "Pubtype": null,
                                  "Prism Teaser": null,
                                  "Dc Description": null,
                                  "Author Keywords": null,
                                  "Prism Aggregation Type": null,
                                  "Prism Copyright": null,
                                  "Scopus Id": null,
                                  "Eid": null,
                                  "Scopus Eid": null,
                                  "Pubmed Id": null,
                                  "Open Access Article": null,
                                  "Open Archive Article": null,
                                  "Open Access User License": null
                              }
                          },
                          {
                              "title": "Chapter 13: Design and management of vehicle-sharing systems: a survey of algorithmic approaches",
                              "authors": [
                                  {
                                      "firstname": "G.",
                                      "initials": null,
                                      "surname": "Pantziou"
                                  },
                                  {
                                      "firstname": "C.",
                                      "initials": null,
                                      "surname": "Konstantopoulos"
                                  },
                                  {
                                      "firstname": "D.",
                                      "initials": null,
                                      "surname": "Gavalas"
                                  }
                              ],
                              "metadata": {
                                  "Source": "Science Direct",
                                  "Links": "[\"https://api.elsevier.com/content/article/pii/B9780128034545000134\",\"https://www.sciencedirect.com/science/article/pii/B9780128034545000134?dgcid=api_sd_search-api-endpoint\"]",
                                  "Load Date": "2016-06-17T00:00:00Z",
                                  "Prism Url": "https://api.elsevier.com/content/article/pii/B9780128034545000134",
                                  "Dc Identifier": "DOI:10.1016/B978-0-12-803454-5.00013-4",
                                  "Open Access": "false",
                                  "Open Access Flag": null,
                                  "Dc Title": "Chapter 13: Design and management of vehicle-sharing systems: a survey of algorithmic approaches",
                                  "Prism Publication Name": "Smart Cities and Homes",
                                  "Prism Isbn": null,
                                  "Prism Issn": null,
                                  "Prism Volume": null,
                                  "Prism Issue Identifier": null,
                                  "Prism Issue Name": null,
                                  "Prism Edition": null,
                                  "Prism Starting Page": "261",
                                  "Prism Ending Page": "289",
                                  "Prism Cover Date": "2016-12-31",
                                  "Prism Cover Display Date": null,
                                  "Dc Creator": "D. Gavalas",
                                  "Authors": "[{\"$\":\"D. Gavalas\"},{\"$\":\"C. Konstantopoulos\"},{\"$\":\"G. Pantziou\"}]",
                                  "Prism Doi": "10.1016/B978-0-12-803454-5.00013-4",
                                  "Pii": "B9780128034545000134",
                                  "Pubtype": null,
                                  "Prism Teaser": null,
                                  "Dc Description": null,
                                  "Author Keywords": null,
                                  "Prism Aggregation Type": null,
                                  "Prism Copyright": null,
                                  "Scopus Id": null,
                                  "Eid": null,
                                  "Scopus Eid": null,
                                  "Pubmed Id": null,
                                  "Open Access Article": null,
                                  "Open Archive Article": null,
                                  "Open Access User License": null
                              }
                          },
                        .....
    
    ```


### Error Report Sample

* Hitting with HTTP GET the endpoint: `http://localhost:8889/repositorer/error-reports/find-all?full-fetch=true`

```json

{
    "retrievedErrorReportDtos": [
        {
            "filename": "ERROR_REPORT2019-04-18T15:38:34.281Z",
            "errorReport": {
                "id": "5cb04b8e-445e-4b9b-990d-2bd883d6387f",
                "createdAt": "2019-04-18T15:38:34.281Z",
                "exceptionToString": "com.eresearch.repositorer.exception.business.RepositorerBusinessException: Could not deserialize message.",
                "repositorerError": "COULD_NOT_DESERIALIZE_MESSAGE",
                "crashedComponentName": "com.eresearch.repositorer.transformer.results.sciencedirect.ScienceDirectResultsTransformer",
                "errorStacktrace": "com.eresearch.repositorer.exception.business.RepositorerBusinessException: Could not deserialize message.\n\tat com.eresearch.repositorer.transformer.results.sciencedirect.ScienceDirectResultsTransformer.transform(ScienceDirectResultsTransformer.java:60)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.inv
                
                ...

```


### Name Lookup Sample

* Hitting with HTTP GET the endpoint: `http://localhost:8889/repositorer/name-lookups/find-all`

```json
{
    "nameLookups": [
        {
            "id": "5cb8b2e21cf6f43290dba9fe",
            "transactionId": "f414a76c-0edb-4da6-b45b-e7c7d5bf6c6a",
            "firstname": "Anastasios",
            "initials": "",
            "surname": "Tsolakidis",
            "nameVariants": [
                {
                    "firstname": "A.",
                    "initials": "",
                    "surname": "Tsolakidis"
                },
                {
                    "firstname": "A",
                    "initials": "",
                    "surname": "Tsolakidis"
                }
            ],
            "nameLookupStatus": "COMPLETED",
            "createdAt": "2019-04-18T17:24:50.865Z"
        }
    ]
}
```


