package org.bklab.quark.entity;

import com.google.gson.JsonArray;
import dataq.core.xml.XmlObject;
import dataq.util.RandomUtil;
import org.bklab.quark.entity.dao.EntityDao;
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
        time.print("解析Entity完毕");
        Connection connection = JdbcConnectionManager.getConnection();
        time.print("创建数据库连接完毕");
    }

    @Test
    void testCreate() {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            entities.add(factory.createEntity("FunctionPoint"));
        }
        time.print("创建100个Entity完毕");
        new EntityDao(entities).createTables(true);
        time.print("创建Entity table 完毕");
    }

    @Test
    void testInsert() throws Exception {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Entity functionPoint = factory.createEntity("FunctionPoint");
            JsonArray array = new JsonArray();
            array.add(1200);
            array.add(1300);
            array.add(1500);
            array.add(400);
            functionPoint.value("functionId", i % 9 + 1)
                    .value("pointId", i % 10)
                    .value("name", "功能点" + (((int) functionPoint.getValue("functionId") * 100) + (int) functionPoint.getValue("pointId")))
                    .value("description", "功能点" + i + "号")
                    .value("type", RandomUtil.nextObject(functionPoint.getProperty("type").getFixValues()))
                    .value("dependFunction", array)
                    .value("updateTime", null)
            ;
            entities.add(functionPoint);
        }
        time.print("创建100个Entity完毕");
        new EntityDao(entities).insert();
        time.print("Entity保存完毕");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        time.print("执行完成");
    }
}