package cn.fancychuan.hbase.tools;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * 用于创建和关闭Connection对象
 *
 *  1. 通过ConnectionFactory.createConnection()来创建
 *  2. Configuration对象：
 *      - 可以使用HBaseConfiguration.create()来创建
 *      - 通过这种方式创建的Configuration对象既包含了hadoop的8个配置文件参数，
 *        也包含了hbase-default.xml以及hbase-site.xml中的所有配置参数
 */
public class ConnectUtil {

    public static Connection getConnection() throws IOException {
        // 方式1
        Connection connection = ConnectionFactory.createConnection();
        return connection;
    }

    public static Connection getConnection2() throws IOException {
        // 方式2
        Configuration conf;
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop101");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        Connection connection = ConnectionFactory.createConnection(conf);
        return connection;
    }

    public static void close(Connection connection) throws IOException {
        if (connection != null) {
            connection.close();
        }
    }


}
