package com.eresearch.repositorer.transformer.results.dblp;

/*
        ~~~~ DBLP IMPORTANT NOTES ~~~~

-> Entities of DBLP which contain the same attributes:
    1) Book
    2) Data
    3) Incollection
    4) Inproceeding
    5) Masterthesis
    6) Phdthesis
    7) Proceeding
    8) Www

-> The Article entity is slightly different from the above it contains also two extra attributes: {reviewId, rating}


-> The Person entity is different from everything else, it contains the following attributes:
    1) key
    2) mdate
    3) cdate
    4) authors
    5) notes
    6) urls
    7) cites
    8) crossrefs

 */
