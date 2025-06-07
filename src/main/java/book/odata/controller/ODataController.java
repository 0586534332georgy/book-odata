package book.odata.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.apache.olingo.server.api.ODataHttpHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/odata")
@RequiredArgsConstructor
public class ODataController {

	private final ODataHttpHandler oDataHttpHandler;

	@RequestMapping(value = "/**")
	public void process(HttpServletRequest request, HttpServletResponse response) {
		try {

			// Let the handler do the work
			oDataHttpHandler.process(new HttpServletRequestWrapper(request) {
				// Spring's HttpServletRequest doesn't behave exactly the same as the default
				// servlet API
				// implementation with regard to URL processing. This fixes that.
				@Override
				public String getServletPath() {
					return "/odata";
				}
			}, response);
		} catch (RuntimeException e) {
			throw new RuntimeException("OData processing failed", e);
		}
	}
}
