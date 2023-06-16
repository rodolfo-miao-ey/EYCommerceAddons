/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package br.com.braspag.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class BraspagcoreHelloController
{
	//@Autowired
	//private BraspagcoreService braspagcoreService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		//model.addAttribute("logoUrl", braspagcoreService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
