package mt356.FIA.application.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mt356.FIA.application.service.Datasource;

/**
 *  This controller interacts with the view, by supplying the view with the data required by the HTML (Thymeleaf) 
 * @author Michael
 *
 */
@Controller
public class HomeController {

	/**
	 * 
	 * @param model : Input data into the model attributes to populate data points in the html
	 * @return home.html : A String indicating which view the controller should direct and show the user.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String homeIndex(Model model) {
		Datasource ds = new Datasource();
		try {
			model.addAttribute("stockNames", ds.getStockList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "home";
	}
	
	/**
	 * The method is used to reload the page when the user chooses an option to change the chart options.
	 * @return "redirect:/" which reloads the page via a redirect updating the webpage
	 */
	@RequestMapping(value="/requestJson", method=RequestMethod.POST)
	public String homeIndexPost() {
		return "redirect:/";
	}
	
}
