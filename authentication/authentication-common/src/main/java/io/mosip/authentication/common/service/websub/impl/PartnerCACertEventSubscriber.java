package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;

/**
 * The Class PartnerCACertEventSubscriber.
 * 
 * @author Manoj SP
 */
@Component
public class PartnerCACertEventSubscriber extends BaseWebSubEventsSubscriber {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(PartnerCACertEventSubscriber.class);

	/** The partner service callback URL. */
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_URL + "}")
	private String partnerCertCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET + "}")
	private String partnerCertCallbackSecret;

	@Value("${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	private String partnerCertEventTopic;

	/**
	 * Do initialize.
	 */
	@Override
	protected void doInitialize() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doInitialize", this.getClass().getSimpleName(),
				"Initializing Partner Certificate event subscriptions..");
		tryRegisterTopicPartnerCertEvents();
		subscribeForPartnerCertEvent();
	}

	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopicPartnerCertEvents() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerCertEvent", "",
					"Trying to register topic: " + partnerCertEventTopic);
			publisher.registerTopic(partnerCertEventTopic, publisherUrl);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerCertEvent", "",
					"Registered topic: " + partnerCertEventTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerCertEvent", e.getClass().toString(),
					"Error registering topic: " + partnerCertEventTopic + "\n" + e.getMessage());
		}
	}

	/**
	 * Subscribe for partner service events.
	 */
	private void subscribeForPartnerCertEvent() {
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(partnerCertCallbackURL);
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(partnerCertCallbackSecret);
			subscriptionRequest.setTopic(partnerCertEventTopic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForPartnerCertEvent", "",
					"Trying to subscribe to topic: " + partnerCertEventTopic + " callback-url: "
							+ partnerCertCallbackURL);
			subscribe.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForPartnerCertEvent", "",
					"Subscribed to topic: " + partnerCertEventTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForPartnerCertEvent", e.getClass().toString(),
					"Error subscribing topic: " + partnerCertEventTopic + "\n" + e.getMessage());
			throw e;
		}
	}
}
