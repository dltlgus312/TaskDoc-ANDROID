package com.service.taskdoc.display.custom.custom;

import com.service.taskdoc.database.transfer.DecisionVO;
import com.service.taskdoc.service.network.restful.service.DecisionItemService;
import com.service.taskdoc.service.network.restful.service.VoterService;

public class DecisionDialog {

    private DecisionVO vo;

    private DecisionItemService decisionItemService;

    private VoterService voterService;

    public DecisionDialog(DecisionVO vo){
        this.vo = vo;
        decisionItemService = new DecisionItemService();
        voterService = new VoterService();
    }
}
