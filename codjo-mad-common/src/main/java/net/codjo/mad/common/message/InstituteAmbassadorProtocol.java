package net.codjo.mad.common.message;
/**
 * Protocol <tt>institute-ambassador-protocol</tt>. Ce protocole défini la comunication nécessaire pour instituer un ambassadeur (cad le login).
 *
 * @see net.codjo.mad.client.plugin.InstituteAmbassadorInitiator
 * @see InstituteAmbassadorAction
 * @see LoginEvent
 */
public interface InstituteAmbassadorProtocol {
    public static final String ID = "institute-ambassador-protocol";
    String SECRETARY_GENERAL_AGENT_NAME = "SecretaryGeneralAgent";
}
