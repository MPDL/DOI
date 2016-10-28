package de.mpg.mpdl.doxi.pidcache;

import java.io.StringReader;
import java.io.StringWriter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLTransforming {
  private static final Logger LOG = LoggerFactory.getLogger(XMLTransforming.class);

  public XMLTransforming() {}

  public PidServiceResponseVO transformToVO(String xml) throws JiBXException {
    if (xml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToVO: pidServiceResponseXml is null");
    }
    
    PidServiceResponseVO pidServiceResponseVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(xml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      pidServiceResponseVO = (PidServiceResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      LOG.error("TRANSFORM:\n{}", e);
      throw e;
    }

    return pidServiceResponseVO;
  }

  public String transformToXML(PidServiceResponseVO pidServiceResponseVO) throws JiBXException {
    if (pidServiceResponseVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + "transformToXML:pidServiceResponseVO is null");
    }
    
    String utf8container = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(pidServiceResponseVO, "UTF-8", null, sw);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      LOG.error("TRANSFORM:\n{}", e);
      throw e;
    }

    return utf8container;
  }
}
