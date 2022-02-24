package com.example.mytextpdf;

import com.example.mytextpdf.utils.DocReader;
import com.example.mytextpdf.utils.PDFContant;
import com.example.mytextpdf.utils.PDFProcess;
import com.example.mytextpdf.utils.TextParser;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;

@SpringBootTest
class MytextpdfApplicationpdfTemplate {

    String staticPath = PDFContant.resourcePath+"pdfTemplate";

    public void test() {
        try {
            InputStream doc_stream = new ClassPathResource("pdfTemplate/融资合同.doc").getInputStream();
            OutputStream xml_stream = new FileOutputStream(staticPath + File.separator + "融资合同.xml");
            OutputStream json_stream = new FileOutputStream(staticPath + File.separator + "融资合同.json");

            DocReader reader = new DocReader();
            reader.setAutoTitle(true);
            reader.ignoreBlankPara(true);
            reader.read(doc_stream, xml_stream, json_stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        try {

            TextParser parser = new TextParser(
                    new FileInputStream(staticPath + File.separator + "融资合同.xml"),
                    new FileInputStream(staticPath + File.separator + "融资合同.json"),
                    new FileOutputStream(staticPath + File.separator + "融资合同.pdf"));
            parser.genPDF();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testPDFProcess() throws Exception {
        PDFProcess pdfProcess = new PDFProcess(
                new FileInputStream(staticPath + File.separator + "融资合同.pdf"),
                new FileOutputStream(staticPath + File.separator + "融资合同2.pdf"));
        pdfProcess.encrypt("passwd", null, PDFProcess.ALLOW_PRINTING);
        //添加水印
//        pdfProcess.addTextMarker("测试水印", 0.2f, 45, 18,PDFProcess.MARKER_STYLE_FULL);
        //添加图片
//        String picurl=staticPath+File.separator+"logo-32.png";
        String picurl = "https://official-minio.zhidianlife.com/jeecg-system_test_1a8a82f683b229e1bc0c144786318533.png";
        pdfProcess.addImgMarker(picurl, -320, 620, 32, 32, 1.0f, 11);
        //添加二维码
//        pdfProcess.addQRCode("qrcode 以及中文");
//		pdfProcess.addHeader("H中文ello");
        //添加页号
        pdfProcess.addPageNum();
        pdfProcess.finish();
    }

}
