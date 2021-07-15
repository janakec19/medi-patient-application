package com.medi.patient.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.medi.patient.model.Patient;
import com.medi.patient.repository.PatientRepository;

@RequestMapping(value = "patients")
@Controller
public class PatientController {

	@Autowired
	private PatientRepository patientRepository;

	@GetMapping(value = "/")
	public String getAllPatients(Model model) {
		model.addAttribute("pageTitle", "Patients | Details");
		model.addAttribute("patients", patientRepository.findAll());
		return "patients/view-patient";
	}

	@GetMapping(value = "/add")
	public String addPatients(Model model) {
		model.addAttribute("pageTitle", "Patients | Add");
		model.addAttribute("patients", patientRepository.findAll());
		return "patients/add-patient";
	}

	@PostMapping(value = "/add")
	public String addPatients(@Valid @ModelAttribute Patient patient, RedirectAttributes redirectAttr,
			BindingResult result) {
		Optional<String> msg = getErrorMessage(result);
		if (!msg.isPresent()) {
			patientRepository.save(patient);
			redirectAttr.addFlashAttribute("status", "success");
			redirectAttr.addFlashAttribute("message", "Patient information successfully saved");
		} else {
			redirectAttr.addFlashAttribute("status", "error");
			redirectAttr.addFlashAttribute("message", msg);
		}
		return "redirect:/patients/add";
	}

	@GetMapping(value = "/edit/{patientId}")
	public String edit(@PathVariable Integer patientId, Model model, RedirectAttributes redirectAttr) {
		try {
			if (patientId != 0) {
				Optional<Patient> patient = patientRepository.findById(patientId);
				if (patient.isPresent()) {
					model.addAttribute("patient", patient.get());
				} else {
					redirectAttr.addFlashAttribute("message", "No patient was matched");
					return "redirect:/patients/";
				}
			} else {
				redirectAttr.addFlashAttribute("message", "No reference was found");
				return "redirect:/patients/";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		model.addAttribute("pageTitle", "Patients | Update");
		return "patients/edit-patient";
	}

	@PostMapping(value = "/update")
	public String update(@Valid @ModelAttribute Patient patient, RedirectAttributes redirectAttr,
			BindingResult result) {
		redirectAttr.addFlashAttribute("status", "error");
		try {
			if (patient != null && patient.getId() != 0) {
				Optional<String> msg = getErrorMessage(result);
				if (!msg.isPresent()) {
					patientRepository.save(patient);
					redirectAttr.addFlashAttribute("status", "success");
					redirectAttr.addFlashAttribute("message", "Patient Info Successfully updated");
					return "redirect:/patients/";
				} else {
					redirectAttr.addFlashAttribute("message", msg);
					return "redirect:/patients/edit/" + patient.getId();
				}
			} else {
				redirectAttr.addFlashAttribute("message", "No reference was found");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return "redirect:/patients/";
	}

	@GetMapping(value = "/delete/{patientId}")
	public String delete(@PathVariable Integer patientId, RedirectAttributes redirectAttr) {
		if (patientId != null) {
			try {
				patientRepository.deleteById(patientId);
				redirectAttr.addFlashAttribute("status", "success");
				redirectAttr.addFlashAttribute("message", "Patient Information successfully deleted");
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return "redirect:/patients/";
	}

	private Optional<String> getErrorMessage(BindingResult result) {
		String msg = "";
		if (result.hasErrors()) {
			FieldError error = null;
			for (Object obj : result.getAllErrors()) {
				error = (FieldError) obj;
				msg += error.getDefaultMessage();
			}
		}
		return msg == "" ? Optional.empty() : Optional.of(msg);
	}
}
