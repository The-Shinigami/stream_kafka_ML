package com.fullstackmaina.streamService.controller;


import com.fullstackmaina.streamService.model.Diabete;
import com.fullstackmaina.streamService.serice.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.swing.text.Position;

@RestController
public class modelController {

    @Autowired
    StreamService streamService;

    @GetMapping("/model/statistics")
    public void getInfo(){
        streamService.createModelForDiabetes();
    }

    @PostMapping("/model/predict")
    public void getPredictions(@RequestBody Diabete diabete){
        streamService.predict(diabete);
    }
}
