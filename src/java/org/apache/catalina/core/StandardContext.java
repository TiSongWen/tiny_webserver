package org.apache.catalina.core;

import org.apache.catalina.*;
import org.apache.catalina.deploy.*;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.RequestUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.resources.BaseDirContext;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.WARDirContext;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import static com.sun.javafx.scene.SceneHelper.setPaused;

/**
 * Created by tisong on 9/3/16.
 */
public class StandardContext
    extends ContainerBase
    implements Context{


    private static final Log logger = LogFactory.getLog(StandardContext.class);

    private boolean available = false;
    private boolean configured = false;
    private boolean override = false;

    private boolean useNaming = true;
    private boolean filesystemBased = false;

    /**
     * TODO 关于 Context 缓存的配置问题
     */
    private boolean cachingAllowed = true;



    private String displayName = null;

    private String docBase = null;

    private String path = null;

    private boolean reloadable = true;



    /**
     * 该 Context 的工作目录: 相对 server home 的路径
     */
    private String workDir = null;


    private Map envs = new HashMap();

    /**
     * name - FilterDef
     */
    private Map<String, FilterDef> filterDefs = new HashMap<String, FilterDef>();
    /**
     * name - ApplicationFilterConfig
     */
    private Map filterConfigs = new HashMap();



    private String mapperClass = "org.apache.catalina.core.StandardContextMapper";

    private String wrapperClass = "org.apache.catalina.core.StandardWrapper";


    /**
     * url pattern - servlet name;(解析web.xml的时候添加对象)
     */
    private Map<String, String> servletMappings = new HashMap<String, String>();
    /**
     * name - wrapper
     */
    private Map<String, Wrapper> children = new HashMap<String, Wrapper>();


    private int sessionTimeout = 30;


    private String[] applicationListeners = new String[0];
    private Object[] applicationListenersObjects = new Object[0];





    private ApplicationContext context =  null;


    private NamingContextListener namingContextListener = null;

    private NamingResources namingResources = new NamingResources();

    private String namingContextName = null;

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
        logger.info("StandardContext 实例化");
    }





    protected void addDefaultMapper(String mapperClass) {
        super.addDefaultMapper(this.mapperClass);
    }


    @Override
    public void addServletMapping(String pattern, String name) {

        System.out.println("addServletMapping的 pattern: " + pattern + "; name: " + name);

        servletMappings.put(pattern, name);

        fireContainerEvent("addServletMapping", pattern);
    }


    @Override
    public String findServletMapping(String pattern) {
        return servletMappings.get(pattern);
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public void removeServletMapping(String pattern) {

    }


    @Override
    public NamingResources getNamingResources() {
        return null;
    }

    @Override
    public void setNamingResources(NamingResources namingResources) {

    }

    @Override
    public String[] findApplicationListeners() {
        return applicationListeners;
    }

    @Override
    public void addApplicationListener(String listener) {

    }

    @Override
    public void removeApplicationListener(String listener) {

    }

    @Override
    public void addConstraint(SecurityConstraint constraint) {

    }

    @Override
    public SecurityConstraint[] findConstraints() {
        return new SecurityConstraint[0];
    }

    @Override
    public void removeConstraint(SecurityConstraint constraint) {

    }

    @Override
    public void getLoginConfig() {

    }

    @Override
    public void setLoginConfig(LoginConfig config) {

    }

    @Override
    public CharsetMapper getCharsetMapper() {
        return null;
    }

    @Override
    public void setCharsetMapper(CharsetMapper mapper) {

    }

    @Override
    public boolean getCookies() {
        return false;
    }

    @Override
    public void setCookies(boolean cookies) {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public void setPublicId(String publicId) {

    }

    @Override
    public void setConfigured(boolean configured) {
        this.configured = configured;
    }
    @Override
    public boolean getConfigured() {
        return this.configured;
    }

    @Override
    public void setOverride(boolean override) {
        this.override = override;
    }

    @Override
    public boolean getOverride() {
        return this.override;
    }


    @Override
    public Wrapper createWrapper() {


        Wrapper wrapper = new StandardWrapper();

        return wrapper;
    }


    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public void addEjb(ContextEjb ejb) {

    }

    @Override
    public ContextEjb findEjb(String name) {
        return null;
    }

    @Override
    public ContextEjb[] findEjbs() {
        return new ContextEjb[0];
    }

    @Override
    public void removeEjb(String name) {

    }

    @Override
    public void addEnvironment(ContextEnvironment environment) {

    }

    @Override
    public ContextEnvironment findEnvironment(String name) {
        return null;
    }

    @Override
    public ContextEnvironment[] findEnvironments() {
        return new ContextEnvironment[0];
    }

    @Override
    public void removeEnvironment(String name) {

    }

    @Override
    public void addLocalEjb(ContextLocalEjb ejb) {

    }

    @Override
    public ContextLocalEjb findLocalEjb(String name) {
        return null;
    }

    @Override
    public ContextLocalEjb[] findLocalEjbs() {
        return new ContextLocalEjb[0];
    }

    @Override
    public void addResource(ContextResource resource) {

    }

    @Override
    public ContextResource findResource(String name) {
        return null;
    }

    @Override
    public ContextResource[] findResources() {
        return new ContextResource[0];
    }

    @Override
    public void removeResource(String name) {

    }

    @Override
    public void addResourceLink(ContextResourceLink resourceLink) {

    }

    @Override
    public ContextResourceLink findResourceLink(String name) {
        return null;
    }

    @Override
    public ContextResourceLink[] findResourceLinks() {
        return new ContextResourceLink[0];
    }

    @Override
    public void removeResourceLink(String name) {

    }

    @Override
    public FilterDef findFilterDef(String filterName) {
        return null;
    }

    @Override
    public FilterDef[] findFilterDefs() {
        return new FilterDef[0];
    }

    @Override
    public void removeFilterDef(FilterDef filterDef) {

    }

    @Override
    public void reload() {


        setPaused(true);

        if (manager != null && manager instanceof Lifecycle) {
            try {
                ((Lifecycle) manager).stop();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

        Container[] children = findChildren();
        for (Container child: children) {
            Wrapper wrapper = (Wrapper)child;
            if (wrapper instanceof Lifecycle) {
                try {
                    ((Lifecycle) wrapper).stop();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        }

        listenerStop();

        if (context != null)
            context.clearAttributes();

        filterStop();


        namingContextListener.lifecycleEvent(new LifecycleEvent(this, Lifecycle.STOP_EVENT));

        if ((loader != null) && (loader instanceof Lifecycle)) {
            try {
                ((Lifecycle) loader).stop();
            } catch (LifecycleException e) {
               // log(sm.getString("standardContext.stoppingLoader"), e);
            }
        }



        // Restart our application class loader
        if ((loader != null) && (loader instanceof Lifecycle)) {
            try {
                ((Lifecycle) loader).start();
            } catch (LifecycleException e) {
                //log(sm.getString("standardContext.startingLoader"), e);
            }
        }


        listenerStart();

        filterStart();


        postResources();
        postWelcomeFiles();

        for (Container child: children) {
            Wrapper wrapper = (Wrapper)child;
            if (wrapper instanceof Lifecycle) {
                try {
                    ((Lifecycle) wrapper).start();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        }

        //loadOnStartup(children);


        if ((manager != null) && (manager instanceof Lifecycle)) {
            try {
                ((Lifecycle) manager).start();
            } catch (LifecycleException e) {
                //log(sm.getString("standardContext.startingManager"), e);
            }
        }

        setPaused(false);

        // Notify our interested LifecycleListeners
        //lifecycleSupport.fireLifecycleEvent(Context.RELOAD_EVENT, null);

    }




    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public void setAvailable(boolean available) {

        this.available = available;
    }

    @Override
    public String getDocBase() {
        return null;
    }

    @Override
    public void setDocBase(String docBase) {

    }

    @Override
    public String getPath() {
        return getName();
    }

    @Override
    public void setPath(String path) {

        setName(RequestUtil.URLDecode(path));
    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public void setReloadable(boolean reloadable) {

    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public Object[] getApplicationListeners() {
        return applicationListenersObjects;
    }

    @Override
    public void setApplicationListeners(Object[] listeners) {

    }


    @Override
    public ServletContext getServletContext() {


        if (context == null) {
            context = new ApplicationContext(getBasePath(), this);
        }

        return context;
    }

    @Override
    public void addLoader(Loader loader) {

    }


    /**
     * 设置 resources
     * 1. 启动Loader
     * 2. 启动Manager
     * 3. 设置 ServletContext 属性
     * 4. JNDI 资源初始化
     * 5. 监听器初始化
     * 6. 过滤器初始化
     * @throws LifecycleException
     */
    public void start() throws LifecycleException {

        if (started) {
            throw new LifecycleException(sm.getString("containerBase.alreadyStarted"));
        }


        lifecycleSupport.fireLifecycleEvent(BEFORE_START_EVENT, null);


        setAvailable(false);
        setConfigured(false);

        boolean ok = true;

        /**
         * Context 所抽象的资源(文件 与 文件夹均看做资源); setResources 会对 WAR 和 File进行封装, 最后的实际对象是: <code>ProxyDirContext</code>
         */
        if (getResources() == null) {
            if (docBase != null && docBase.endsWith(".war")) {
                setResources(new WARDirContext());
            } else {
                setResources(new FileDirContext());
            }
        }

        /**
         * 设置默认的类加载器
         */
        if (getLoader() == null) {
            setLoader(new WebappLoader(getParentClassLoader()));
        }

        /**
         * 设置默认的 Session管理器
         */
        if (getManager() == null) {
            setManager(new StandardManager());
        }


        if (ok) {
           // DirContextURLStreamHandler.bind(getResources());
        }

        /**
         * 创建 CharsetMapper 实例; 暂时不清楚该实例使用在哪里
         */
        getCharsetMaper();

        /**
         * ServletContext 属性设置
         */
        postWorkDirectory();


        String useNamingProperty = System.getProperty("catalina.useNaming");
        if ( ("false").equals(useNamingProperty)) {
            useNaming = false;
        } else if (ok && namingContextListener == null){
            namingContextListener = new NamingContextListener();
            namingContextListener.setName(getNamingContextName());
            addLifecycleListener(namingContextListener);
        }



        if (ok) {
            super.start();
        }

        if (!getConfigured() ) {
            ok = false;
        }



        /**
         * 初始化与 Context 关联的 JNDI 资源
         */
        if (ok && isUseNaming()) {
            createNamingContext();
        }

        // We put the resources into the servlet context
        if (ok) {
            getServletContext().setAttribute
                    (Globals.RESOURCES_ATTR, getResources());
        }



        if (ok && !listenerStart() && !filterStart() ) {
            ok = false;
        }

        if (ok) {
            postWelcomeFiles();
            loadOnStartUp(findChildren());

            setAvailable(true);
        } else {
            stop();
            setAvailable(false);
        }

        lifecycleSupport.fireLifecycleEvent(AFTER_START_EVENT, null);
    }


    private void getCharsetMaper() {

    }

    /**
     * 设置 ServletContext 属性
     * eg: javax.servlet.context.tempdir(每一个ServletContext 都需要临时存储目录，关联的对象必须是java.io.File 类型)
     */
    private void postWorkDirectory() {



        String parentName = null;
        if (getParent() != null) {
            parentName = getParent().getName();
        }
        if (parentName == null || parentName.length() < 1) {
            parentName = "_";
        }

        String workDir = getWorkDir();
        if (workDir == null) {


            String temp = getPath();
            if (temp.startsWith("/")) {
                temp = temp.substring(1);
            }
            temp.replace("/", "_");
            temp.replace("\\", "_");
            if (temp.length() < 1) {
                temp = "_";
            }
            workDir = "work" + File.separator + parentName + File.separator + temp;

            setWorkDir(workDir);
        }

        /** 为 ServletContext 创建临时工作目录 **/
        File dir = new File(workDir);
        if (!dir.isAbsolute()) {
            // 如果不是绝对路径, 则要转变为绝对路径
            File catalinaHome = new File(System.getProperty("catalina.base"));
            String catalinaHomePath = null;
            try {
                catalinaHomePath = catalinaHome.getCanonicalPath();
                dir = new File(catalinaHomePath, workDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dir.mkdirs();

        this.getServletContext().setAttribute(Globals.WORK_DIR_ATTR, dir);
        if (this.getServletContext() instanceof ApplicationContext) {
//            ((ApplicationContext) getServletContext()).setAttributeReadOnly
//                    (Globals.WORK_DIR_ATTR);
        }

    }


    private void postResources() {
        getServletContext().setAttribute
                (Globals.RESOURCES_ATTR, getResources());
    }



    /**
     * 将 FitlerDef 转化为 ApplicationFilterConfig(封装了一个Filter对象)
     * @return
     */
    public boolean filterStart() {

        boolean ok = true;

        filterConfigs.clear();

        Iterator<String> names = filterDefs.keySet().iterator();
        while (names.hasNext()) {

            String name = names.next();
            ApplicationFilterConfig filterConfig = null;
            try {
                filterConfig = new ApplicationFilterConfig(
                        this, filterDefs.get(name));
                filterConfigs.put(name, filterConfig);
            } catch (Throwable t) {
                t.printStackTrace();
                ok = false;
            }
        }

        return ok;
    }


    public boolean filterStop() {

        return true;
    }

    /**
     * 创建 Listener对象
     * @return
     */
    public boolean listenerStart() {

        boolean ok = true;

        String[] listeners = findApplicationListeners();
        Object[] results = new Object[listeners.length];

        for (String listener: listeners) {

        }

        setApplicationListeners(results);
        Object[] instances = getApplicationListeners();


        ServletContextEvent event = new ServletContextEvent(getServletContext());
        for (Object instance : instances) {

            ServletContextListener listener = (ServletContextListener) instance;

            listener.contextInitialized(event);
        }

        return ok;
    }



    public boolean listenerStop() {
        return true;
    }

    private void postWelcomeFiles() {
//        getServletContext().setAttribute("org.apache.catalina.WELCOME_FILES",
//                welcomeFiles);

    }


    private void createNamingContext() {

    }


    public void loadOnStartUp(Container[] children) {

    }


    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getWorkDir() {
        return workDir;
    }


    public String getBasePath() {

        return "";
    }


    public boolean isUseNaming(){
        return useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }




    @Override
    public void addApplicationParameter(ApplicationParameter parameter) {
        // TODO
    }

    @Override
    public ApplicationParameter[] findApplicationParameters() {
        return new ApplicationParameter[0];
    }

    @Override
    public void removeApplicationParameter(String name) {

    }




    public void setResources(DirContext resource) {

        if (resources instanceof BaseDirContext) {
            ((BaseDirContext) resources).setDocBase(getBasePath());
            ((BaseDirContext) resources).setCached(isCachingAllowed());
        }

        if (resources instanceof FileDirContext) {
            filesystemBased = true;
        }

        super.setResources(resources);
        if (started) {
            getServletContext().setAttribute
                    (Globals.RESOURCES_ATTR, getResources());
        }
    }


    public boolean isCachingAllowed() {
        return cachingAllowed;
    }

    public void setCachingAllowed(boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
    }


    /**
     * 获取 naming context 全称
     */
    private String getNamingContextName() {

        if (namingContextName == null) {
            Container parent = getParent();
            if (parent == null) {
                namingContextName = getName();
            } else {
                Stack stk = new Stack();
                StringBuffer buff = new StringBuffer();
                while (parent != null) {
                    stk.push(parent.getName());
                    parent = parent.getParent();
                }
                while (!stk.empty()) {
                    buff.append("/" + stk.pop());
                }
                buff.append(getName());
                namingContextName = buff.toString();
            }
        }
        return namingContextName;
    }
}
