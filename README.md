This is a simple implemenation of a [Spark](http://sparkjava.com/) Template Engine for [Thymeleaf](http://www.thymeleaf.org) (v3.0). 

It is customized for our needs so that when appropriate keys are in the `ModelAndView` object to be rendered (`#request` and `#response`), the Thymeleaf Template Engine will attempt to create a `WebContext`. Otherwise, it will fallback to a standard `Context` so it will be rendered.
