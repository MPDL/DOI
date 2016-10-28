package de.mpg.mpdl.doxi.pidcache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class InitializerServlet extends HttpServlet {
  private PidCacheTask pidCacheTask;
  private PidQueueTask pidQueueTask;

  public final void init() throws ServletException {
    super.init();

    this.pidCacheTask = new PidCacheTask();
    this.pidCacheTask.start();

    this.pidQueueTask = new PidQueueTask();
    this.pidQueueTask.start();
  }

  public void destroy() {
    super.destroy();

    this.pidCacheTask.interrupt();
    this.pidQueueTask.interrupt();
  }
}
