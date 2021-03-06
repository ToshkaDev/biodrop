package controller;

import biojobs.BioDrop;
import biojobs.Tab;
import biojobs.TabDao;
import enums.BioPrograms;
import exceptions.IncorrectRequestException;
import model.request.EvolutionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.BioTaskLongRunningService;
import org.json.*;
import service.StorageService;
import serviceimpl.BioTaskServiceImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class BioTaskController extends BioUniverseController {
    public final BioTaskServiceImpl bioTaskServiceImpl;
    public final BioTaskLongRunningService bioTaskLongRunningService;
    public final TabDao tabDao;
    private static final String basicReturn = "main-viewDrop  :: addContent(" +
            "subNavbarAndInputFragments='subNavbarAndInputs', paramFragments='parameters', searchArea='selectSearch', " +
            "tab='subTab-navbar', params='program-parameters')";
    private static final String[] searchAreas = {"searchArea-multiple-inputs", "searchArea-two-inputs", "searchArea-one-input"};

    @Autowired
    public BioTaskController(StorageService storageService, BioTaskServiceImpl bioTaskServiceImpl,
                             BioTaskLongRunningService bioTaskLongRunningService,TabDao tabDao) {
        super(storageService);
        this.bioTaskServiceImpl = bioTaskServiceImpl;
        this.bioTaskLongRunningService = bioTaskLongRunningService;
        this.tabDao = tabDao;
    }

    @GetMapping(value={"", "/"})
    public String getHome() {
        //TODO
        return "Home";
    }

    @GetMapping(value={"/{tab:tab.+}"})
    public String getTab(@PathVariable("tab") String tabName, Model model) {
        addToModelCommon(model);
        Tab tab = tabDao.findByTabName(tabName);
        List<String> subTabs = new LinkedList<>();
        tab.getBioDropList().forEach(biodrop -> subTabs.add(biodrop.getSubTabName()));


        return null;

    }

    //Ex., /tab_evolution/subTab_createCogs
    @GetMapping(value={"/{tab:tab.+}/{subTab:subTab.+}"})
    public String getByNamePage(@PathVariable("tab") String tabName, @PathVariable("subTab") String subTabName, Model model) {
        addToModelCommon(model);
        BioDrop bioDrop = bioTaskServiceImpl.getBioDropDao().findBySubTabName(subTabName);
        Tab tab = tabDao.findByTabName(tabName);
        JSONObject programParams = new JSONObject(bioDrop.getProgramParameters());
        String view;

//        List<Object> usualParams = programParams.getJSONArray("usualParams").toList();
//        JSONObject selectionPrams = programParams.getJSONObject("selectionPrams");
//        JSONObject radioParams = programParams.getJSONObject("radioParams");
//        JSONObject checkBoxParams = programParams.getJSONObject("checkBoxParams");

        model.addAttribute("programParams", programParams);
        model.addAttribute("tabs", tabDao.findAll());
        model.addAttribute("mainTab", tabName);
        model.addAttribute("bioDropList", tab.getBioDropList());
        model.addAttribute("subnavigationTab", subTabName);

        if (bioDrop.getNumberOfInputs() == 1) {
            view = basicReturn.replace("selectSearch", searchAreas[2]);
        } else if (bioDrop.getNumberOfInputs() == 2) {
            view = basicReturn.replace("selectSearch", searchAreas[1]);
        } else {
            view = basicReturn.replace("selectSearch", searchAreas[0]);
        }

        return view;
    }

    //Ex., /tab_evolution/process-request
    @PostMapping(value="/{tab:tab.+}/process-request", produces="text/plain")
    @ResponseBody
    public String processRequest(@PathVariable("tab") String tab, EvolutionRequest evolutionRequest) throws IncorrectRequestException {
        return null;
    }

    @Override
    public void addToModelCommon(Model model) {

    }
}
