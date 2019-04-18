package com.eresearch.repositorer.extractor;

import java.util.List;

/*
  The class which will implements this, will be published by default (via spring boot) as JMX Monitoring Bean (MBean).
 */
public interface BatchExtractorMBean {

    List<String> getEntriesWaitingToBeProcessed();
}
