package filter;


import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

public class RequestIdFilter implements Filter
{

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {

  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    try {
      MDC.put("RequestId", SID.getSID());
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  @Override
  public void destroy()
  {

  }

}
