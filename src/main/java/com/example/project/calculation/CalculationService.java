package com.example.project.calculation;

import com.example.project.employeedetails.EmployeeDetails;
import com.example.project.employeedetails.EmployeeDetailsRepository;
import com.example.project.exception.ResoruceNotFoundException;
import com.example.project.hrfeedback.HrFeedback;
import com.example.project.hrfeedback.HrFeedbackRepository;
import com.example.project.mentorfeedback.MentorFeedback;
import com.example.project.mentorfeedback.MentorFeedbackRepository;
import com.example.project.peerfeedback.PeerFedback;
import com.example.project.peerfeedback.PeerFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CalculationService {
    @Autowired
    private HrFeedbackRepository hrFeedbackRepository;

    @Autowired
    private MentorFeedbackRepository mentorFeedbackRepository;

    @Autowired
    private PeerFeedbackRepository peerFeedbackRepository;

    @Autowired
    private EmployeeDetailsRepository employeeDetailsRepository;

    @Autowired
    private CalculationRepository calculationRepository;

    Calculation a = new Calculation();

    public ResponseEntity<Calculation> getCalculatedValues(int empid) throws ResoruceNotFoundException {
        Calculation calculation = new Calculation();
        double behaviour = 0, communication = 0, extrawork = 0, responsibility = 0, deadline = 0, workload = 0, extraactivity = 0;
        int tot_behaviour = 0, tot_communication = 0, tot_extrawork = 0, tot_responsibility = 0, tot_deadline = 0, tot_workload = 0, tot_extraactivity = 0;
        List<HrFeedback> hrFeedbackList = hrFeedbackRepository.getHrfeedbackByEmpId(empid);
        for (HrFeedback hrFeedback : hrFeedbackList) {
            behaviour += (hrFeedback.getBehaviour());
            communication += (hrFeedback.getCommunication());
            extraactivity += (hrFeedback.getExtraactivity());

            tot_behaviour+=5;
            tot_extraactivity+=5;
            tot_communication+=5;
        }

        List<MentorFeedback> mentorFeedbackList = mentorFeedbackRepository.getHrfeedbackByEmpId(empid);
        for (MentorFeedback mentorFeedback : mentorFeedbackList) {
            communication += (mentorFeedback.getCommunication());
            behaviour += (mentorFeedback.getBehaviour());
            extrawork += (mentorFeedback.getExtrawork());
            deadline += (mentorFeedback.getDeadline());
            workload += (mentorFeedback.getWorkload());
            responsibility += (mentorFeedback.getResponsibility());

            tot_behaviour+=5;
            tot_extrawork+=5;
            tot_communication+=5;
            tot_responsibility+=5;
            tot_workload+=5;
            tot_deadline+=5;
        }

        List<PeerFedback> peerFedbackList = peerFeedbackRepository.getPeerFeedbackOfAnEmployee(empid);
        for (PeerFedback peerFedback : peerFedbackList) {
            communication += (peerFedback.getCommunication());
            behaviour += (peerFedback.getBehaviour());
            responsibility += (peerFedback.getResponsibility());

            tot_behaviour+=5;
            tot_communication+=5;
            tot_responsibility+=5;
        }
        int x=0;
        double tot_sum=0;
        if(tot_extrawork!=0)
        {
            tot_sum+=(extrawork/tot_extrawork);
            x++;
        }
        if(tot_behaviour!=0)
        {
            tot_sum+=(behaviour/tot_behaviour);
            x++;
        }
        if(tot_communication!=0)
        {
            tot_sum+=(communication/tot_communication);
            x++;
        }
        if(tot_deadline!=0)
        {
            tot_sum+=(deadline/tot_deadline);
            x++;
        }
        if(tot_workload!=0)
        {
            tot_sum+=(workload/tot_workload);
            x++;
        }
        if(tot_responsibility!=0)
        {
            tot_sum+=(responsibility/tot_responsibility);
            x++;
        }
        if(tot_extraactivity!=0)
        {
            tot_sum+=(extraactivity/tot_extraactivity);
            x++;
        }

        double percentile = (tot_sum/x)*100;

        calculation.setEmpid(empid);
        calculation.setExtraactivity((int)Math.round((extraactivity/tot_extraactivity)*5));
        calculation.setBehaviour((int)Math.round((behaviour/tot_behaviour)*5));
        calculation.setResponsibility((int)Math.round((responsibility/tot_responsibility)*5));
        calculation.setCommunication((int)Math.round((communication/tot_communication)*5));
        calculation.setDeadline((int)Math.round((deadline/tot_deadline)*5));
        calculation.setExtrawork((int)Math.round((extrawork/tot_extrawork)*5));
        calculation.setWorkload((int)Math.round((workload/tot_workload)*5));
        calculation.setPercentile(percentile);
        calculationRepository.save(calculation);
        return ResponseEntity.ok(calculation);
    }

    public List<Calculation> getStatByTeam(int teamid) throws ResoruceNotFoundException
    {
        List<Calculation> calculationList = new ArrayList<>();
        List<EmployeeDetails> employeeDetailsList=employeeDetailsRepository.getemployeeBYTeam(teamid,"member");
        for(EmployeeDetails employeeDetails: employeeDetailsList)
        {
            int empid= employeeDetails.getEmpid();
//            Optional<Calculation> calculation=this.calculationRepository.findById(empid);
//            if(calculation.isEmpty())
//            {
//                continue;
//            }
            Calculation calculation1 = this.getCalculatedValues(empid).getBody();
            calculationList.add(calculation1);
        }
        return calculationList;
    }

    public List<Calculation> getOrderedStats() throws ResoruceNotFoundException
    {
        List<Calculation> calculationList = this.calculationRepository.getOrderedStats();
        return calculationList;
    }
}



