package com.tk.mybatis;

import com.tk.mybatis.binding.MapperProxyFactory;
import com.tk.mybatis.dao.IUserDao;
import com.tk.mybatis.io.Resources;
import com.tk.mybatis.session.SqlSession;
import com.tk.mybatis.session.SqlSessionFactory;
import com.tk.mybatis.session.SqlSessionFactoryBuilder;
import com.tk.mybatis.util.ClassScanner;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

/**
 * @Author liuht
 * @Date 2023/7/29 17:13
 * @Version 1.0
 * @Description 描述
 */
//@SpringBootTest
public class ApiTest {
    public static final Logger log= LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void mapperInterfaceProxyTest(){
        MapperProxyFactory<IUserDao> mapperProxyFactory = new MapperProxyFactory(IUserDao.class);
        HashMap<String, String> sqlSession = new HashMap<>();
        sqlSession.put("com.tk.mybatis.dao.IUserDao.queryUserById","模拟执行的xml,查询用户信息");
        sqlSession.put("com.tk.mybatis.dao.IUserDao.queryUserAge","模拟执行的xml 查询用户年龄");

//        IUserDao userDao =  mapperProxyFactory.newInstance(sqlSession);
//        String result = userDao.queryUserAge("1011");
//        System.out.println("测试运行的结果："+result);
    }

    @Test
    public void scannerTest(){
        String packagePath = "com.tk.mybatis.binding";
        Set<Class<?>> classes = ClassScanner.scanPackge(packagePath);
        System.out.println(classes);
    }

    /*@Test
    public void sessionTest(){
        //1、注册Mapper
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("com.tk.mybatis.dao");

        //2、获取sqlSession
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession sqlSession = defaultSqlSessionFactory.openSession();

        //3、获取映射器
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        System.out.println(userDao.queryUserAge("123"));
    }*/


    @Test
    public void xmlParserTest(){
        try {
            //1、创建xml文件读取的io流
            Reader resoucres = Resources.getResoucres("mybatis-config-datasource.xml");
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();

            //2、解析xml，组装mapper配置类
            SqlSessionFactory sqlSessionFactory = builder.build(resoucres);

            //3、获取sqlSession
            SqlSession sqlSession = sqlSessionFactory.openSession();

            //4、sqlSession获取对应的dao接口
            IUserDao userDao = sqlSession.getMapper(IUserDao.class);

            //5、执行方法
            String result = userDao.queryUserAge("10010");
            log.info("执行结果：{}",result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
