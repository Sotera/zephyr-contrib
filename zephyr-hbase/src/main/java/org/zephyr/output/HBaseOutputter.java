package org.zephyr.output;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.zephyr.data.Entry;
import org.zephyr.data.Record;

public class HBaseOutputter extends Outputter {

	private String zookeeperQuorum;
	private String hbaseMaster;
	private String hbaseTableName;
	private HTable hbaseTable;
	
	private static final byte[] columnFamily = "column".getBytes();
	
	public HBaseOutputter(final String zookeeperQuorum, final String hbaseMaster, final String hbaseTableName) {
		this.zookeeperQuorum = zookeeperQuorum;
		this.hbaseMaster = hbaseMaster;
		this.hbaseTableName = hbaseTableName;
	}
	
	public void initialize() throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", zookeeperQuorum);  // Here we are running zookeeper locally
		conf.set("hbase.master", hbaseMaster);
		hbaseTable = new HTable(conf, hbaseTableName);
	}
	
	@Override
	public void output(Record record) throws Exception {
		Put put = new Put(record.getUuid().getBytes());
		for (Entry entry : record) {
			put.add(columnFamily, entry.getLabel().getBytes(), entry.getValue().getBytes());
		}
		this.hbaseTable.put(put);		
	}

	@Override
	public void close() throws IOException {
		this.hbaseTable.close();
	}

	@Override
	public void flush() throws IOException {
		this.hbaseTable.flushCommits();
	}

}
