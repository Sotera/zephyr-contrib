Zephyr Contrib
------------------------

Zephyr contrib contains some potentially useful zephyr components that you may want to include in your Zephyr ETL processes - these components are used in many scenarios, but generally come with extensive dependencies that probably shouldn't be in the core API.

Current modules include:
 - zephyr-secure : A conversion utility to take org.zephyr.data.Records and convert them into a Accumulo Visibility respecting data object (for authorization limited in-memory real-time analytics)
 - zephyr-hbase : An org.zephyr.output.Outputter implementation of an HBase outputter (primarily for example and testing purposes)
