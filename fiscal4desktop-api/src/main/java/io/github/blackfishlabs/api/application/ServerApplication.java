package io.github.blackfishlabs.api.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

public class ServerApplication {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);
    private static Component restServer;

    public static void main(String[] args) {
        System.out.println(start(8182));
        restart();
        System.out.println(stop());
    }

    public static String start(int port) {
        try {
            restServer = new Component();
            Server server = restServer.getServers().add(Protocol.HTTP, port);

            JaxRsApplication application = new JaxRsApplication(restServer.getContext());

            application.add(new ApplicationFiscal());

            restServer.getDefaultHost().attach(application);
            restServer.start();
            return ">> Service started on port " + server.getPort();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public static String stop() {
        try {
            restServer.stop();
            return ">> NFeServer stopped with success";
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    private static void restart() {
        logger.info(stop());
        logger.info(start(8182));
    }
}
