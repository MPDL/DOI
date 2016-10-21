package de.mpg.mpdl.doxi.pidcache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitializerServlet extends HttpServlet {
  private CacheTask cacheTask;
  private QueueTask queueTask;

  public final void init() throws ServletException {
    super.init();

    this.cacheTask = new CacheTask();
    this.cacheTask.start();

    this.queueTask = new QueueTask();
    this.queueTask.start();
  }

  public void destroy() {
    super.destroy();
    this.cacheTask.terminate();
    this.queueTask.terminate();
  }
}
