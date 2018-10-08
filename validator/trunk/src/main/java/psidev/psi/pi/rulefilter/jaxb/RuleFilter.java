//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.09.21 um 11:21:11 AM CEST 
//


package psidev.psi.pi.rulefilter.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}userConditions"/&gt;
 *         &lt;element ref="{}ruleConditions" minOccurs="0"/&gt;
 *         &lt;element ref="{}references" minOccurs="0"/&gt;
 *         &lt;element ref="{}mandatoryElements" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "userConditions",
    "ruleConditions",
    "references",
    "mandatoryElements"
})
@XmlRootElement(name = "ruleFilter")
public class RuleFilter {

    @XmlElement(required = true)
    protected UserConditions userConditions;
    protected RuleConditions ruleConditions;
    protected References references;
    protected MandatoryElements mandatoryElements;

    /**
     * Ruft den Wert der userConditions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UserConditions }
     *     
     */
    public UserConditions getUserConditions() {
        return userConditions;
    }

    /**
     * Legt den Wert der userConditions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UserConditions }
     *     
     */
    public void setUserConditions(UserConditions value) {
        this.userConditions = value;
    }

    /**
     * Ruft den Wert der ruleConditions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RuleConditions }
     *     
     */
    public RuleConditions getRuleConditions() {
        return ruleConditions;
    }

    /**
     * Legt den Wert der ruleConditions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleConditions }
     *     
     */
    public void setRuleConditions(RuleConditions value) {
        this.ruleConditions = value;
    }

    /**
     * Ruft den Wert der references-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link References }
     *     
     */
    public References getReferences() {
        return references;
    }

    /**
     * Legt den Wert der references-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link References }
     *     
     */
    public void setReferences(References value) {
        this.references = value;
    }

    /**
     * Ruft den Wert der mandatoryElements-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MandatoryElements }
     *     
     */
    public MandatoryElements getMandatoryElements() {
        return mandatoryElements;
    }

    /**
     * Legt den Wert der mandatoryElements-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MandatoryElements }
     *     
     */
    public void setMandatoryElements(MandatoryElements value) {
        this.mandatoryElements = value;
    }

}
