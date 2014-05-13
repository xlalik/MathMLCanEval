/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.mir.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cz.muni.fi.mir.db.domain.Annotation;
import cz.muni.fi.mir.db.domain.AnnotationFlag;
import cz.muni.fi.mir.db.domain.CanonicOutput;
import cz.muni.fi.mir.db.service.AnnotationFlagService;
import cz.muni.fi.mir.db.service.AnnotationService;
import cz.muni.fi.mir.db.service.CanonicOutputService;
import cz.muni.fi.mir.db.service.UserService;
import cz.muni.fi.mir.forms.AnnotationForm;
import cz.muni.fi.mir.pagination.Pagination;
import cz.muni.fi.mir.tools.EntityFactory;
import cz.muni.fi.mir.wrappers.SecurityContextFacade;


/**
 *
 * @author siska
 */
@Controller
@RequestMapping(value = "/canonicoutput")
public class CanonicOutputController
{
    @Autowired
    private CanonicOutputService canonicOutputService;
    @Autowired
    private AnnotationFlagService annotationFlagService;
    @Autowired
    private AnnotationService annotationService;
    @Autowired
    private UserService userService;

    @Autowired
    private Mapper mapper;
    @Autowired
    private SecurityContextFacade securityContext;

    @RequestMapping(value = {"/view/{id}", "/view/{id}/"}, method = RequestMethod.GET)
    public ModelAndView viewCanonicOutput(@PathVariable Long id)
    {
        ModelMap mm = new ModelMap();
        CanonicOutput canonicOutput = canonicOutputService.getCanonicOutputByID(id);

        mm.addAttribute("formulaEntry", canonicOutput);
        mm.addAttribute("annotationForm", new AnnotationForm());
        mm.addAttribute("annotationFlagList", annotationFlagService.getAllAnnotationFlags());

        long totalAnnotations = 0;
        Map<Long, Long> annotationFlagHits = new HashMap<>();
        if (canonicOutput != null) 
        {
            for (Annotation annotation : canonicOutput.getAnnotations())
            {
                Long index = annotation.getAnnotationFlag().getId();

                if (!annotationFlagHits.containsKey(index))
                    annotationFlagHits.put(index, 0L);
                annotationFlagHits.put(index, annotationFlagHits.get(index) + 1);
                totalAnnotations += 1;
            }
        }
        mm.addAttribute("totalAnnotations", totalAnnotations);
        mm.addAttribute("annotationFlagHits", annotationFlagHits);

        return new ModelAndView("canonicoutput_view", mm);
    }

    @Secured("ROLE_ADMINISTRATOR")
    @RequestMapping(value={"/delete/{id}","/delete/{id}/"},method = RequestMethod.GET)
    public ModelAndView deleteCanonicOutput(@PathVariable Long id)
    {
        canonicOutputService.deleteCanonicOutput(canonicOutputService.getCanonicOutputByID(id));
        return new ModelAndView("redirect:/");
    }

    @Secured("ROLE_USER")
    @RequestMapping(value={"/annotate","/annotate/"},method = RequestMethod.POST)
    public ModelAndView annotate(@RequestParam("canonicOutputId") String canonicOutputId, @ModelAttribute("annotationForm") AnnotationForm annotationForm)
    {
        Annotation annotation = EntityFactory.createAnnotation();
        annotation.setUser(userService.getUserByUsername(securityContext.getLoggedUser()));
        annotation.setAnnotationFlag(mapper.map(annotationForm.getAnnotationFlagForm(), AnnotationFlag.class));
        annotation.setNote(annotationForm.getNote());
        annotationService.createAnnotation(annotation);

        CanonicOutput canonicOutput = canonicOutputService.getCanonicOutputByID(Long.valueOf(canonicOutputId));
        List<Annotation> annotations = new ArrayList<>(canonicOutput.getAnnotations());
        annotations.add(annotation);
        canonicOutput.setAnnotations(annotations);

        canonicOutputService.updateCanonicOutput(canonicOutput);

        return new ModelAndView("redirect:/canonicoutput/view/" + canonicOutputId);
    }

    @RequestMapping(value = {"/similar/{id}","/similar/{id}/"},method = RequestMethod.GET)
    public ModelAndView searchResults(@ModelAttribute("pagination") Pagination pagination, @PathVariable Long id, Model model)
    {
        CanonicOutput canonicOutput = canonicOutputService.getCanonicOutputByID(id);        
        ModelMap mm = new ModelMap();
        mm.addAttribute("pagination", pagination);
        mm.addAttribute("reference", canonicOutput);
        mm.addAttribute("outputList", canonicOutputService.getSimilarCanonicOutputs(canonicOutput, 
                (pagination.getPageNumber() - 1) * pagination.getPageSize(), pagination.getPageSize()));

        return new ModelAndView("canonicoutput_list",mm);
    }
}
