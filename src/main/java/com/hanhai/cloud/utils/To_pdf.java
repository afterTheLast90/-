package com.hanhai.cloud.utils;

import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;

import java.io.File;
import java.util.concurrent.*;

public class To_pdf {
    private static LocalOfficeManager manager;
    static {
//        manager  = LocalOfficeManager.builder().officeHome("C:\\Users\\dell\\Desktop\\kekingcn-file-online-preview-master\\file-online-preview\\office-plugin\\windows-office")
//                .taskExecutionTimeout(1000*20).portNumbers(9003,9004,9005).taskQueueTimeout(1000*60*60).install().build();

        manager  = LocalOfficeManager.builder().officeHome(System.getProperty("user.dir")+"/src/main/resources/kekingcn-file-online-preview-master/file-online-preview/office-plugin/windows-office")
                .taskExecutionTimeout(1000*20).portNumbers(9003,9004,9005).taskQueueTimeout(1000*60*60).install().build();
        try {
            manager.start();
        } catch (OfficeException e) {
            e.printStackTrace();
        }

//        Callable<String> task=new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                manager.start();
//                return "success";
//            }
//        };
//        ExecutorService executorService= Executors.newSingleThreadExecutor();
//        Future<String> future=executorService.submit(task);
//        try{
//            String result =future.get(10, TimeUnit.)
//        }
//        try {
//            manager.start();
//        } catch (OfficeException e) {
//            e.printStackTrace();
//        }
    }

    public static void To_Pdf(String path, String newPath) throws OfficeException {

//        try {
            JodConverter.convert(new File(path)).to(new File(newPath)).execute();
//        } catch (OfficeException e) {
//            throw e;
//        }

    }

}