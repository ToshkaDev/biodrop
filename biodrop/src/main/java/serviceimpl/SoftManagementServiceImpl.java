package serviceimpl;

import biojobs.BioDrop;
import biojobs.BioDropDao;
import biojobs.Tab;
import biojobs.TabDao;
import enums.DropOperationStatus;
import model.request.BioDropRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import service.SoftManagementService;
import service.StorageService;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class SoftManagementServiceImpl implements SoftManagementService {

    private final String defaultTab = "defaultPrograms";
    private final String defaultTabName = "Miscellaneous programs";
    private final BioDropDao bioDropDao;
    private final TabDao tabDao;
    private final StorageService storageService;

    @Autowired
    public SoftManagementServiceImpl(BioDropDao bioDropDao, TabDao tabDao, StorageService storageService) {
        this.bioDropDao = bioDropDao;
        this.storageService = storageService;
        this.tabDao = tabDao;
    }


    @Override
    public String dropProgram(BioDropRequest bioDropRequest) throws IOException {
        DropOperationStatus status = saveProgram(bioDropRequest);

        /*if (status.equals(DropOperationStatus.OK)) {
            installDependencies(bioDropRequest);
            installInterpreter(bioDropRequest);
        }*/

        return status.getStatus();
    }


    private DropOperationStatus saveProgram(BioDropRequest bioDropRequest) throws IOException {
        BioDrop bioDrop = bioDropDao.findBySubTab(bioDropRequest.getSubTab());
        if (bioDrop != null) {
            return DropOperationStatus.SUBTAB_EXISTS;
        }

        StringBuilder programAsStringBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (bioDropRequest.getProgram().getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                programAsStringBuilder.append((char) c);
            }
        }

        bioDrop = bioDropDao.findByProgram(programAsStringBuilder.toString());
        if (bioDrop != null) {
            return DropOperationStatus.PROGRAM_EXISTS;
        }

        BioDrop bioDropNew = new BioDrop();
        bioDropNew.setLongRunning(bioDropRequest.isLongRunning());
        bioDropNew.setProgram(programAsStringBuilder.toString());
        String programName = bioDropRequest.getProgramName()+"_"+UUID.randomUUID().toString();
        bioDropNew.setProgramName(programName);
        bioDropNew.setScriptName(programName);
        bioDropNew.setNumberOfInputs(bioDropRequest.getNumberOfInputs());
        bioDropNew.setInputFilePrefixes(bioDropRequest.getInputFilePrefixes());
        bioDropNew.setOutputFilePrefixes(bioDropRequest.getOutputFilePrefixes());
        bioDropNew.setSubTab(bioDropRequest.getSubTab());
        bioDropNew.setSubTabLink("/" + bioDropRequest.getTab() + "/" + bioDropRequest.getSubTab());

        bioDropNew.setProgramLanguage(bioDropRequest.getProgramLanguage());
        bioDropNew.setProgramParameters(bioDropRequest.getProgramParameters());
        bioDropNew.setProgramDependencies(bioDropRequest.getProgramDependencies());
        bioDropNew.setProgramInstallInstructs(bioDropRequest.getProgramInstallInstructs());

        String tabName = bioDropRequest.getTab() != null ? bioDropRequest.getTab() : defaultTab;
        String tabText = bioDropRequest.getTab() != null ? bioDropRequest.getTabText() : defaultTabName;
        analyzeAndSaveTab(tabName, tabText, bioDropNew);

        storageService.storeProgram(bioDropRequest.getProgram(), bioDropNew.getProgramName());
        bioDropDao.save(bioDropNew);

        return DropOperationStatus.OK;
    }

    private void analyzeAndSaveTab(String tabName,  String tabText, BioDrop bioDropNew) {
        Tab existingTab = tabDao.findByTabName(tabName);
        if (existingTab == null) {
            Tab tab = new Tab();
            tab.setTabName(tabName);
            tab.setTabText(tabText);
            tab.setTabLink("/" + tabName);
            bioDropNew.setTab(tab);
        } else {
            bioDropNew.setTab(existingTab);
        }
    }
    //TODO
    /*private void installDependencies(BioDropRequest bioDropRequest) {

    }

    private void installInterpreter(BioDropRequest bioDropRequest) {

    }*/
}
