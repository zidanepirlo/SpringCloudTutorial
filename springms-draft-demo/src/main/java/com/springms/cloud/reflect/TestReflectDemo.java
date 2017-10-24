package com.springms.cloud.reflect;

import com.springms.cloud.reflect.util.AnnotationReflectParser;
import com.springms.cloud.reflect.util.xml.BeanXml;

/**
 * 测试类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class TestReflectDemo {

    public static void main(String[] args) {

        try {
            String xmlData = getXml();
            Class<?> beanClass = getBeanClassPath();

            Object respBankDTO = BeanXml.xml2Bean(xmlData, beanClass);

            ResultDTO resultObject = new ResultDTO();
            ResultDTO.Record record = new ResultDTO.Record();
            resultObject.setRecord(record);

            boolean finished = AnnotationReflectParser.start(respBankDTO, resultObject);
            System.out.println("finished: " + finished);
            System.out.println("=====================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里可以通过路径反射得到 Class 类，如果你的类有规律的话，那完成可以在这个地方通过设定规则出来得到类名路径。
     *
     * 那么我这里呢，就直接拿个例子来试试而已。
     *
     * @return
     */
    private static Class<?> getBeanClassPath() {
        String className = "com.springms.cloud.reflect.BankDTO";
        return getRespBeanClass(className);
    }

    private static String getXml() {
        String recvContent = "<?xml version='1.0' encoding='GB2312'?>\n" +
                "<packet>\n" +
                "<head>\n" +
                "<transCode>4469</transCode>  \n" +
                "<signFlag>0</signFlag>   \n" +
                "<packetID>1234567890</packetID>      \n" +
                "<timeStamp>2004-07-28 16:14:29</timeStamp> \n" +
                "<returnCode>AAAAAAA</returnCode>  \n" +
                "</head>\n" +
                "<body>\n" +
                "<acctNo>246333388999</acctNo>\n" +
                "<acctName>张三</acctName>\n" +
                "<acctBalance>199098777.97</acctBalance>\n" +
                "<subTotBalance>199098777.97</subTotBalance>\n" +
                "<lists name=\"LoopResult\">\n" +
                "<list>\n" +
                "<subAcctNo>1234567890000000</subAcctNo>\n" +
                "<subAcctBalance>234.56</subAcctBalance>\n" +
                "<subAcctName>账户名称甲</subAcctName>\n" +
                "</list>\n" +
                "</lists>\n" +
                "</body>\n" +
                "</packet>";

        return recvContent;
    }

    /**
     * 获取响应类名的 Class 对象。
     *
     * @return
     */
    private static Class<?> getRespBeanClass(String className) {
        Class<?> respClass = null;
        try {
            respClass = Class.forName(className);
            return respClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(className + " 该响应类路径不存在", e);
        }
    }
}
