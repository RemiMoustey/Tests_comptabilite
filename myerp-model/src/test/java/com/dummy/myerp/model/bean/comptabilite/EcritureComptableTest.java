package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) throws Exception {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null  ? null : new BigDecimal(pCredit);
        if (vDebit != null && vCredit != null || vDebit == null && vCredit == null) {
            throw new Exception("Une ligne doit créditer ou débiter");
        }
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void isGettingTotalDebit() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "130"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "130", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "55.25", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "55.25"));
        BigDecimal total = new BigDecimal(130 + 55.25).setScale(2, RoundingMode.CEILING);
        Assert.assertEquals(total, vEcriture.getTotalDebit());
    }

    @Test
    public void isGettingTotalCredit() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "100"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "15.25", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "15.25"));
        BigDecimal total = new BigDecimal(100 + 15.25).setScale(2, RoundingMode.CEILING);
        Assert.assertEquals(total, vEcriture.getTotalCredit());
    }

    @Test
    public void isEquilibreeRG2() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "301", null));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());
    }

    @Test
    public void isNotEquilibreeRG2() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "11"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "30", null));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

    @Test
    public void areMontantsSignesRG4() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "-130"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "-130", null));
        Assert.assertEquals(new BigDecimal(-130).setScale(2, RoundingMode.CEILING), vEcriture.getTotalCredit());
        Assert.assertEquals(new BigDecimal(-130).setScale(2, RoundingMode.CEILING), vEcriture.getTotalDebit());
    }

    @Test
    public void checkNumbersAfterCommaRG7() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "130.245"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "130.245", null));
        Assert.assertEquals(new BigDecimal(130.25), vEcriture.getListLigneEcriture().get(0).getCredit());
        Assert.assertEquals(new BigDecimal(130.25), vEcriture.getListLigneEcriture().get(1).getDebit());
    }
}