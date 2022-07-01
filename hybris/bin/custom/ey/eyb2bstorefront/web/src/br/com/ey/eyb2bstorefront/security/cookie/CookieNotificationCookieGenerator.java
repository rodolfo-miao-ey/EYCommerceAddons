/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package br.com.ey.eyb2bstorefront.security.cookie;


/**
 * CookieGenerator for CookieNotification overlay.
 *
 */
public class CookieNotificationCookieGenerator extends EnhancedCookieGenerator
{
	private static final String COOKIE_NOTIFICATION = "cookie-notification";

	@Override
	public String getCookieName()
	{
		return COOKIE_NOTIFICATION;
	}
}
