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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class BioTaskController extends BioUniverseController {
    public final BioTaskServiceImpl bioTaskServiceImpl;
    public final BioTaskLongRunningService bioTaskLongRunningService;
    public final TabDao tabDao;

    @Autowired
    public BioTaskController(StorageService storageService, BioTaskServiceImpl bioTaskServiceImpl, BioTaskLongRunningService bioTaskLongRunningService,TabDao tabDao) {
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
        tab.getBioDropList().forEach(biodrop -> subTabs.add(biodrop.getSubTab()));


        return null;

    }

    //Ex., /tab_evolution/subTab_createCogs
    @GetMapping(value={"/{tab:tab.+}/{subTab:subTab.+}"})
    public String getByNamePage(@PathVariable("tab") String tabName, @PathVariable("subTab") String subTab, Model model) {
        addToModelCommon(model);
        BioDrop bioDrop = bioTaskServiceImpl.getBioDropDao().findBySubTab(subTab);
        List<Tab> allTabs = tabDao.findAll();
        Tab tab = tabDao.findByTabName(tabName);

        int inputNum = bioDrop.getNumberOfInputs();

        JSONObject programParams = new JSONObject(bioDrop.getProgramParameters());


        List<Object> usualParams = programParams.getJSONArray("usualParams").toList();
        JSONObject selectionPrams = programParams.getJSONObject("selectionPrams");
        JSONObject radioParams = programParams.getJSONObject("radioParams");
        JSONObject checkBoxParams = programParams.getJSONObject("checkBoxParams");


        model.addAttribute("programParams", usualParams);




        List<BioDrop> bioDropList = tab.getBioDropList();




/*        "selectionParamName"
        "selectionParamId"
        "selectionParams :" "selectionParam.value" "selectionParam.text"


        "programParams :" "param.paramName"


        "radioParamName"
        "radioParams :" "radioParam.paramName" "radioParam.value"*/




        List<String> subTabsNames = new LinkedList<>();
        List<String> subTabsLinks = new LinkedList<>();

        for (BioDrop biodrop : tab.getBioDropList()) {
            subTabsNames.add(biodrop.getSubTab());
            subTabsLinks.add(biodrop.getSubTabLink());
        }

        model.addAttribute("subnavigationTab", BioPrograms.CREATE_COGS.getProgramName());

        return "main-viewDrop  :: addContent(" +
                "subNavbarAndInputFragments='subNavbarAndInputs', paramFragments='parameters', searchArea='searchArea-multiple-inputs', " +
                "tab='subTab-navbar', params='program-parameters-forOne-input')";
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
