FROM hirokimatsumoto/alpine-openjdk-11

EXPOSE 9001

ARG JAR_FILE

WORKDIR /app

COPY ./target/${JAR_FILE} .


RUN echo "#!/bin/sh" > run.sh \
 && echo "java -Xms64m -Xmx256m -server -jar ${JAR_FILE}" >> run.sh \
 && chmod +x run.sh


ENTRYPOINT ["./run.sh"]
