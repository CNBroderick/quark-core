package org.bklab.quark.entity;

import dataq.core.xml.XmlObject;
import org.bklab.quark.service.JdbcConnectionManager;
import org.bklab.quark.util.RunningTime;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

class EntitySchemaFactoryTest {

    private XmlObject xmlObject;
    private EntitySchemaFactory factory;
    private Entity entity;
    private RunningTime time;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        time = new RunningTime();
        xmlObject = XmlObject.fromFile("D:\\broderick\\develop\\bklab\\quark\\src\\main\\java\\org\\bklab\\quark\\entity\\entity.xml");
        factory = new EntitySchemaFactory(xmlObject);
        time.print("解析Entity");
        Connection connection = JdbcConnectionManager.getConnection();
        time.print("创建数据库连接");
    }

    @Test
    void testCreate() {
        entity = factory.createEntity("FunctionPoint");

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> entity.nextEntityInstanceId());
            threads.add(thread) ;
            thread.start();
        }

        while (threads.stream().anyMatch(Thread::isAlive)) {

        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.out.println(entity.getEntityInstanceId());
        time.print("执行100次");
    }
}