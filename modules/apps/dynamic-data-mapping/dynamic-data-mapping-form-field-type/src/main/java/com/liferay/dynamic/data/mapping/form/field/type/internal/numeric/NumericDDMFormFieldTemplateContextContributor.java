/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.NumericDDMFormFieldUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true,
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.NUMERIC,
	service = {
		DDMFormFieldTemplateContextContributor.class,
		NumericDDMFormFieldTemplateContextContributor.class
	}
)
public class NumericDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Locale locale = ddmFormFieldRenderingContext.getLocale();

		return HashMapBuilder.<String, Object>put(
			"confirmationErrorMessage",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "confirmationErrorMessage")
		).put(
			"confirmationLabel",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "confirmationLabel")
		).put(
			"dataType", getDataType(ddmFormField, ddmFormFieldRenderingContext)
		).put(
			"direction", ddmFormField.getProperty("direction")
		).put(
			"hideField",
			GetterUtil.getBoolean(ddmFormField.getProperty("hideField"))
		).put(
			"inputMask",
			GetterUtil.getBoolean(ddmFormField.getProperty("inputMask"))
		).put(
			"inputMaskFormat",
			() -> {
				if (!GetterUtil.getBoolean(
						ddmFormField.getProperty("inputMask"))) {

					return StringPool.BLANK;
				}

				return DDMFormFieldTypeUtil.getPropertyValue(
					ddmFormField, locale, "inputMaskFormat");
			}
		).put(
			"placeholder",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "placeholder")
		).put(
			"predefinedValue",
			getFormattedValue(
				ddmFormFieldRenderingContext, locale,
				DDMFormFieldTypeUtil.getPredefinedValue(
					ddmFormField, ddmFormFieldRenderingContext))
		).put(
			"requireConfirmation",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("requireConfirmation"))
		).put(
			"symbols", getSymbolsMap(locale)
		).put(
			"tooltip",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, locale, "tooltip")
		).put(
			"value",
			() -> {
				String value = HtmlUtil.extractText(
					ddmFormFieldRenderingContext.getValue());

				if (Objects.equals(value, "NaN")) {
					return StringPool.BLANK;
				}

				return getFormattedValue(
					ddmFormFieldRenderingContext, locale, value);
			}
		).build();
	}

	protected String getDataType(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> changedProperties =
			(Map<String, Object>)ddmFormFieldRenderingContext.getProperty(
				"changedProperties");

		if (MapUtil.isNotEmpty(changedProperties)) {
			String dataType = (String)changedProperties.get("dataType");

			if (dataType != null) {
				return dataType;
			}
		}

		return ddmFormField.getDataType();
	}

	protected String getFormattedValue(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext,
		Locale locale, String value) {

		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		if (GetterUtil.getBoolean(
				ddmFormFieldRenderingContext.getProperty("valueChanged"))) {

			DecimalFormat decimalFormat =
				NumericDDMFormFieldUtil.getDecimalFormat(locale);

			return decimalFormat.format(GetterUtil.getNumber(value));
		}

		return value;
	}

	protected Map<String, String> getSymbolsMap(Locale locale) {
		DecimalFormat decimalFormat = NumericDDMFormFieldUtil.getDecimalFormat(
			locale);

		DecimalFormatSymbols decimalFormatSymbols =
			decimalFormat.getDecimalFormatSymbols();

		return HashMapBuilder.put(
			"decimalSymbol",
			String.valueOf(decimalFormatSymbols.getDecimalSeparator())
		).put(
			"thousandsSeparator",
			String.valueOf(decimalFormatSymbols.getGroupingSeparator())
		).build();
	}

}