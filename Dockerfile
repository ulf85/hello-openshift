FROM websphere-liberty:javaee8

ENV WLP_ROOT=/opt/ibm/wlp
ENV SRV_ROOT=${WLP_ROOT}/usr/servers/defaultServer

RUN mkdir /tmp/config-data
RUN chmod -R 777 ${WLP_ROOT} /logs /tmp/config-data

COPY target/hello-openshift.war /config/dropins/
