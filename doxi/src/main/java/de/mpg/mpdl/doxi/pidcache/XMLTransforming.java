package de.mpg.mpdl.doxi.pidcache;

import java.io.StringReader;
import java.io.StringWriter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class XMLTransforming {

  public XMLTransforming() {}

  public PidServiceResponseVO transformToVO(String xml) {
    if (xml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPidServiceResponse: pidServiceResponseXml is null");
    }
    PidServiceResponseVO pidServiceResponseVO = null;

    try {
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(xml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      pidServiceResponseVO = (PidServiceResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // // throw a new UnmarshallingException, log the root cause of the JiBXException first
      // logger.error(e.getRootCause());
      // throw new UnmarshallingException(pidServiceResponseXml, e);
    }

    return pidServiceResponseVO;
  }

  public String transformToXML(PidServiceResponseVO pidServiceResponseVO) {
    if (pidServiceResponseVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + "transformToPidServiceResponse:pidServiceResponseVO is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(pidServiceResponseVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      // throw new MarshallingException(pidServiceResponseVO.getClass().getSimpleName(), e);
    }

    return utf8container;
  }

}
