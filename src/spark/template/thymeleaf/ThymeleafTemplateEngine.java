package spark.template.thymeleaf;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import spark.ModelAndView;
import spark.TemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This custom Thymeleaf Template Engine for the Spark Web Framework implements a method of
 * creating a WebContext renderer for our purposes.
 *
 */
public class ThymeleafTemplateEngine extends TemplateEngine
{
  private static final String DEFAULT_PREFIX = "templates/" ;
  private static final String DEFAULT_SUFFIX = ".html" ;
  private static final String DEFAULT_TEMPLATE_MODE = "HTML5" ;
  private static final long DEFAULT_CACHE_TTL_MS = 3600000L ;

  private org.thymeleaf.TemplateEngine engine ;

  public ThymeleafTemplateEngine()
  {
    this(DEFAULT_PREFIX, DEFAULT_SUFFIX) ;
  } ;

  public ThymeleafTemplateEngine(String prefix, String suffix)
  {
    //FileTemplateResolver templateResolver = new FileTemplateResolver() ;
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver() ;
    templateResolver.setTemplateMode(DEFAULT_TEMPLATE_MODE);
    templateResolver.setPrefix((prefix == null || prefix.isEmpty()) ? DEFAULT_PREFIX : prefix) ;
    templateResolver.setSuffix((suffix == null || suffix.isEmpty()) ? DEFAULT_SUFFIX : suffix) ;
    templateResolver.setCacheTTLMs(DEFAULT_CACHE_TTL_MS) ;

    engine = new org.thymeleaf.TemplateEngine() ;
    engine.setTemplateResolver(templateResolver);
  }

  /**
   * Our implementation of the render method is a bit of a hack in that it looks at the ModelView to identify
   * if there is an {@link HttpServletRequest} and an {@link HttpServletResponse}, and if so will create a
   * {@link org.thymeleaf.context.WebContext}, otherwise it will fallback to a default context
   *
   * (TODO: Do we really want to do this? If we cannot create a WebContext, should we fail?)
   *
   * @param modelAndView
   *   The {@link ModelAndView} object to render
   * @return
   *   The processed template representation
   */
  @SuppressWarnings("unchecked")
  public String render(ModelAndView modelAndView)
  {
    String processed = "" ;
    Object model = modelAndView.getModel() ;

    // To render with Thymeleaf, our Model must be a Map that is key/values for the variables to be passed to the template
    if (!(model instanceof Map))
    {
      throw new IllegalArgumentException("The model must be a java.util.Map") ;
    }

    Map<String, Object> map = (Map<String, Object>) model ;

    if ((map.containsKey("#request") && (map.get("#request") instanceof HttpServletRequest)) &&
        (map.containsKey("#response") && (map.get("#response") instanceof HttpServletResponse)))
    {
      HttpServletRequest req = (HttpServletRequest) map.remove("#request") ;
      HttpServletResponse resp = (HttpServletResponse) map.remove("#response") ;
      WebContext ctx = new WebContext(req, resp, req.getSession().getServletContext(), req.getLocale(), map) ;

      processed = engine.process(modelAndView.getViewName(), ctx) ;
    }
    else
    {
      Context ctx = new Context() ;
      ctx.setVariables(map);

      processed = engine.process(modelAndView.getViewName(), ctx) ;
    }

    return processed ;
  }
}
