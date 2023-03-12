package com.microsoft.eventhubplugin;

import liqp.Template;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.jmeter.services.FileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.commons.io.FilenameUtils;
import com.microsoft.DataGenerator;

public class MessageRenderer {
    private static final Logger log = LoggerFactory.getLogger(EventHubPlugin.class);

    private Template template;
    private String fileContent;

    public MessageRenderer(String fileName) {
        try {
            System.out.println(fileName);
            GuiPackage guiPackage = GuiPackage.getInstance();

            if (guiPackage != null) {
                String testPlanFile = guiPackage.getTestPlanFile();
                String testPlanFileDir = FilenameUtils.getFullPathNoEndSeparator(testPlanFile);
                template = Template.parse(new File(testPlanFileDir + "/" + fileName));
            } else {
                log.info("File location" + FileServer.getFileServer().getBaseDir() + "/" + fileName);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FileServer.getFileServer().getBaseDir() + "/" + fileName),
                                "UTF-8"));
                StringBuilder strBuilder = new StringBuilder();
                String curLine;
                while ((curLine = bufferedReader.readLine()) != null) {
                    strBuilder.append("\n");
                    strBuilder.append(curLine);
                }

                bufferedReader.close();

                fileContent = strBuilder.toString();

                System.out.println(fileContent);

                template = Template.parse(fileContent);
            }

        } catch (Exception ex) {
            log.error(fileName, ex);
        }

    }

    public String Render() {
        DataGenerator dataGenerator = new DataGenerator();
        return template.render(true, "dataGenerator", dataGenerator);
    }

}
