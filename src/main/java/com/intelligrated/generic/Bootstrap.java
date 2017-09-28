package com.intelligrated.generic;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.jar.JarFile;

/**
 * Created by sachin.subhedar on 01/19/2017.
 * <p>
 * Copyright (c) 2001-2016 Intelligrated [https://www.intelligrated.com/]
 * <p>
 * The  information  contained  herein  is  the  confidential  and  proprietary
 * information of Intelligrated.  This information is protected,  among others,
 * by the patent,  copyright,  trademark,  and trade secret laws of  the United
 * States and its several states.  Any use,  copying, or reverse engineering is
 * strictly prohibited. This software has been developed at private expense and
 * accordingly,  if used under Government  contract,  the use,  reproduction or
 * disclosure  of  this  information  is subject to  the restrictions set forth
 * under the  contract between  Intelligrated  and its customer.  By viewing or
 * receiving this information, you consent to the foregoing.
 */
public class Bootstrap extends JarLauncher {

    private static ClassLoader classLoader = null;
    private static Bootstrap bootstrap = null;

    protected void launch(String[] args, String mainClass, ClassLoader classLoader, boolean wait)
            throws Exception {
        Runnable runner = createMainMethodRunner(mainClass, args, classLoader);
        Thread runnerThread = new Thread(runner);
        runnerThread.setContextClassLoader(classLoader);
        runnerThread.setName(Thread.currentThread().getName());
        runnerThread.start();
        if (wait) {
            runnerThread.join();
        }
    }

    public static void start (String []args) {
        bootstrap = new Bootstrap ();
        try {
            JarFile.registerUrlProtocolHandler();
            classLoader = bootstrap.createClassLoader(bootstrap.getClassPathArchives());
            bootstrap.launch(args, bootstrap.getMainClass(), classLoader, true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void stop (String []args) {
        try {
            if (bootstrap != null) {
                bootstrap.launch(args, bootstrap.getMainClass(), classLoader, true);
                bootstrap = null;
                classLoader = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0] : null;
        if ("start".equals(mode)) {
            Bootstrap.start(args);
        }
        else if ("stop".equals(mode)) {
            Bootstrap.stop(args);
        }
    }
}
