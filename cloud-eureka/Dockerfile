FROM hirokimatsumoto/alpine-openjdk-11

EXPOSE 9002

ARG JAR_FILE
ARG CFG_URL

WORKDIR /app

COPY ./target/${JAR_FILE} .


RUN echo "#!/bin/sh"                                         > run.sh \
 && echo "until wget ${CFG_URL} ; do"                       >> run.sh \
 && echo "	>&2 echo 'cloud-config Not Ready'"              >> run.sh \
 && echo "	sleep 1"                                        >> run.sh \
 && echo "done"                                             >> run.sh \
 && echo ""                                                 >> run.sh \
 && echo "java -Xms64m -Xmx256m -server -jar ${JAR_FILE}"   >> run.sh \
 && chmod +x run.sh


ENTRYPOINT ["./run.sh"]