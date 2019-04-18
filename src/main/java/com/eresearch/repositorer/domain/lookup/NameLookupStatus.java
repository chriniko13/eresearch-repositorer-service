package com.eresearch.repositorer.domain.lookup;

public enum NameLookupStatus {

    PENDING, /* Under process, data retrieval, etc. from the system. */
    COMPLETED, /* Operation completed successfully. */
    TIMED_OUT /* Operation timed out, means completed unsuccessfully (eg: due to aggregator group timeout, system failure, unexpected exception, etc.). */
}
