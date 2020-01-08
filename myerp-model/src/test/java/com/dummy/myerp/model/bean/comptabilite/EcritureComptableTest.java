package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

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
    public void isEquilibree() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "200.50"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "301", null));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "11"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "30", null));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

//    @Test
//    public void isACorrectEcritureComptable() throws Exception {
//        EcritureComptable vEcriture = new EcritureComptable();
//
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "130"));
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, "130", null));
//        Assert.assertTrue(vEcriture.toString(), vEcriture.isACorrectEcritureComptable());
//        vEcriture.getListLigneEcriture().clear();
//
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, "130", null));
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, "130", null));
//        Assert.assertFalse(vEcriture.toString(), vEcriture.isACorrectEcritureComptable());
//        vEcriture.getListLigneEcriture().clear();
//
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "130"));
//        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "130"));
//        Assert.assertFalse(vEcriture.toString(), vEcriture.isACorrectEcritureComptable());
//    }

    @Test
    public void areMontantsSignes() throws Exception {
        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "-130"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "-130", null));
        Assert.assertEquals(new BigDecimal(-130).setScale(2, RoundingMode.CEILING), vEcriture.getTotalCredit());
        Assert.assertEquals(new BigDecimal(-130).setScale(2, RoundingMode.CEILING), vEcriture.getTotalDebit());
    }

//    @Test
//    public void isACorrectReference() throws Exception {
//        EcritureComptable vEcriture = new EcritureComptable();
//
//        JournalComptable vJournal = new JournalComptable("BQ", "Journal de la banque");
//        SequenceEcritureComptable vSequence = new SequenceEcritureComptable(2016, 1);
//        vEcriture.setReference(vJournal.getCode(), Integer.toString(vSequence.getAnnee()), String.format("%05d", vSequence.getDerniereValeur()));
//        Assert.assertTrue(Pattern.compile("\\w{1,5}-\\d{4}/\\d{5}").matcher(vEcriture.getReference()).matches());
//
//        vJournal = new JournalComptable("BQ", "Journal de la banque");
//        vSequence = new SequenceEcritureComptable(2016, 1485694);
//        vEcriture.setReference(vJournal.getCode(), Integer.toString(vSequence.getAnnee()), String.format("%05d", vSequence.getDerniereValeur()));
//        Assert.assertFalse(Pattern.compile("\\w{1,5}-\\d{4}/\\d{5}").matcher(vEcriture.getReference()).matches());
//    }
}