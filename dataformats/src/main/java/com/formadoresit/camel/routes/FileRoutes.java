package com.formadoresit.camel.routes;

import com.formadoresit.camel.domain.User;
import com.formadoresit.camel.dtos.OrderDto;
import com.formadoresit.camel.dtos.UserDto;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

@Component
public class FileRoutes extends RouteBuilder {
    private final CsvDataFormat csvDataFormat;

    public FileRoutes(){
        csvDataFormat = new CsvDataFormat();
        csvDataFormat.setUseMaps(true);
    }

    @Override
    public void configure() throws Exception {
        from("file:data?move=.procesados&moveFailed=.error")
                .routeId("file-processor")
                .to("direct:processFile");

        from("sftp:{{app.sftp.uri}}&move=.procesadosSftp&moveFailed=.errorSftp")
                .log("Procesando desde SFTP")
                .to("direct:processFile");

        from("direct:processFile")
                .choice()
                    .when(header(Exchange.FILE_NAME).regex("^.*\\.json"))
                        .log("procesando JSON")
                        .to("direct:processFileToObjectJson")
                    .when(header(Exchange.FILE_NAME).regex("^.*\\.xml"))
                        .log("procesando XML")
                        .to("direct:processFileToObjectXml")
                    .when(header(Exchange.FILE_NAME).regex("^.*\\.csv"))
                        .log("procesando CSV")
                        .to("direct:processFileToObjectCsv")
                    .otherwise()
                        .log(LoggingLevel.ERROR, "Formato no soportado")
                .end();

        from("direct:processFileToObjectJson")
                .routeId("process-file-to-object-json")
                .log("procesando body de tipo ${body.class}")
                .unmarshal().json(UserDto.class)
                .log("post body de tipo ${body.class}, userId ${body.id}");

        from("direct:processFileToObjectXml")
                .routeId("process-file-to-object-xml")
                .log("procesando body de tipo ${body.class}")
                .unmarshal().jaxb(User.class.getPackageName())
                .log("post body de tipo ${body.class}, userId ${body.id}");

        from("direct:processFileToObjectCsv")
                .routeId("process-file-to-object-csv")
                .log("procesando body de tipo ${body.class}")
                .unmarshal(csvDataFormat)
                .log("post body de tipo ${body.class}, ${body}");
    }
}
