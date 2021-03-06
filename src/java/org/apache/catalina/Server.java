package org.apache.catalina;

import org.apache.catalina.deploy.NamingResources;

import java.io.IOException;

/**
 * 服务器: 提供一套机制来启动和停止整个系统; 而不必对容器和连接器单独的启动;
 * Created by tisong on 9/6/16.
 */
public interface Server {

    public int getPort();
    public void setPort(int port);


    public String getShutdown();
    public void setShutdown(String shutdown);


    public void await() throws IOException;

    public void addService(Service service);
    public Service[] findServices();
    public void removeService(Service service);


    public void initialize() throws LifecycleException;




    public NamingResources getGlobalNamingResources();
    public void setGlobalNamingResources(NamingResources namingResources);


}
