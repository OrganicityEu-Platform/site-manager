package fr.cea.organicity.manager.controllers.ui;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.experimentlister.Experiment;
import fr.cea.organicity.manager.services.experimentlister.ExperimentLister;
import fr.cea.organicity.manager.services.userlister.UserLister;


@Controller
@RequestMapping("/experiments")
public class ExperimentsControllerUI {

	@Autowired private ExperimentLister experimentLister;
	@Autowired private UserLister userLister;
	
	private final String title = "Experiments";
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	public String experiments(HttpServletRequest request, Model model) {
		List<Experiment> experiments = experimentLister.getExperiments().getLastSuccessResult();
		model.addAttribute("title", title);
		model.addAttribute("experiments", experiments);
		return "thexperiments";
	}
	
	@GetMapping("{experimentId}")
	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	public String experiment(HttpServletRequest request, @PathVariable("experimentId") String experimentId, Model model) throws IOException {
		Experiment experiment = experimentLister.getExperiment(experimentId).getLastSuccessResult();
		model.addAttribute("title", title);
		model.addAttribute("exp", experiment);
		model.addAttribute("experimenters", getExperimenters(experiment));
		model.addAttribute("datasources", experimentLister.getDataSrcByExperiment(experimentId));
		
		return "thexperimentdetails";		
	}
	
	public List<String> getExperimenters(Experiment experiment) {
		return experiment.getExperimenterIds().stream().map(sub -> userLister.getUserNameOrSub(sub)).collect(Collectors.toList());
	}
}
