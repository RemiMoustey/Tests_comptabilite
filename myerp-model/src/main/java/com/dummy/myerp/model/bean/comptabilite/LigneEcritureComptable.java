package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.dummy.myerp.model.validation.constraint.MontantComptable;
import com.dummy.myerp.technical.exception.FunctionalException;


/**
 * Bean représentant une Ligne d'écriture comptable.
 */
public class LigneEcritureComptable {

    // ==================== Attributs ====================
    /** Compte Comptable */
    @NotNull
    private CompteComptable compteComptable;

    /** The Libelle. */
    @Size(max = 200)
    private String libelle;

    /** The Debit. */
    @MontantComptable
    private BigDecimal debit;

    /** The Credit. */
    @MontantComptable
    private BigDecimal credit;


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Ligne ecriture comptable.
     */
    public LigneEcritureComptable() {
    }

    /**
     * Instantiates a new Ligne ecriture comptable.
     *
     * @param pCompteComptable the Compte Comptable
     * @param pLibelle the libelle
     * @param pDebit the debit
     * @param pCredit the credit
     */
    public LigneEcritureComptable(CompteComptable pCompteComptable, String pLibelle,
                                  BigDecimal pDebit, BigDecimal pCredit) throws FunctionalException {
        compteComptable = pCompteComptable;
        libelle = pLibelle;
        debit = pDebit;
//        if (pDebit != null) {
//            String splitCommaDebit = String.valueOf(pDebit);
//            if (splitCommaDebit.contains(".")) {
//                String partsNumberDebit = splitCommaDebit.substring(splitCommaDebit.indexOf("."));
//                if (partsNumberDebit.length() <= 3) {
//                    debit = pDebit;
//                } else {
//                    throw new FunctionalException("Le montant du débit ne peut comporter que deux chiffres maximum après la virgule");
//                }
//            }
//        }

//        if (pCredit != null) {
//            String splitCommaCredit = String.valueOf(pCredit);
//            if (splitCommaCredit.contains(".")) {
//                String partsNumberCredit = splitCommaCredit.substring(splitCommaCredit.indexOf("."));
//                if (partsNumberCredit.length() <= 3) {
//                    credit = pCredit;
//                } else {
//                    throw new FunctionalException("Le montant du crédit ne peut comporter que deux chiffres maximum après la virgule");
//                }
//            }
//        }
        credit = pCredit;
    }


    // ==================== Getters/Setters ====================
    public CompteComptable getCompteComptable() {
        return compteComptable;
    }
    public void setCompteComptable(CompteComptable pCompteComptable) {
        compteComptable = pCompteComptable;
    }
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String pLibelle) {
        libelle = pLibelle;
    }
    public BigDecimal getDebit() {
        return debit;
    }
    public void setDebit(BigDecimal pDebit) {
        debit = pDebit;
    }
    public BigDecimal getCredit() {
        return credit;
    }
    public void setCredit(BigDecimal pCredit) {
        credit = pCredit;
    }


    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
            .append("compteComptable=").append(compteComptable)
            .append(vSEP).append("libelle='").append(libelle).append('\'')
            .append(vSEP).append("debit=").append(debit)
            .append(vSEP).append("credit=").append(credit)
            .append("}");
        return vStB.toString();
    }
}
