package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComptabiliteDaoImplIntegrationTest extends AbstractDbConsumer {

    private static ComptabiliteDaoImpl comptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

    @BeforeClass
    public static void setup() {
        new ClassPathXmlApplicationContext("applicationContext.xml");

    }

    @Test
    public void testGetListCompteComptable() {
        Assert.assertEquals(7, comptabiliteDaoImpl.getListCompteComptable().size());
    }

    @Test
    public void testGetListJournalComptable() {
        Assert.assertEquals(4, comptabiliteDaoImpl.getListJournalComptable().size());
    }

    @Test
    public void testGetListEcritureComptable() {
        Assert.assertEquals(5, comptabiliteDaoImpl.getListEcritureComptable().size());
    }

    @Test
    public void testGetListLigneEcritureComptable() {
        Assert.assertEquals(13, comptabiliteDaoImpl.getListLigneEcritureComptable().size());
    }

    @Test
    public void testGetListSequenceEcritureComptable() {
        Assert.assertEquals(4, comptabiliteDaoImpl.getListSequenceEcritureComptable().size());
    }

    @Test
    public void testGetEcritureComptable_NotFound() {
        try {
            comptabiliteDaoImpl.getEcritureComptable(-9);
        } catch (NotFoundException e) {
            assert(e.getMessage().contains("EcritureComptable non trouvée : id=-9"));
        }
    }

    @Test
    public void testGetEcritureComptable_Found() throws NotFoundException {
        Assert.assertEquals(comptabiliteDaoImpl.getEcritureComptable(-4).getLibelle(), "TMA Appli Yyy");
    }

    @Test
    public void testGetEcritureComptableByRef_NotFound() {
        try {
            comptabiliteDaoImpl.getEcritureComptableByRef("AC-2016/00044");
        } catch (NotFoundException e) {
            assert(e.getMessage().contains("EcritureComptable non trouvée : reference=AC-2016/00044"));
        }
    }

    @Test
    public void testGetEcritureComptableByRef_Found() throws NotFoundException {
        Assert.assertEquals(comptabiliteDaoImpl.getEcritureComptableByRef("AC-2016/00001").getLibelle(), "Cartouches d’imprimante");
    }

    @Test
    public void testLoadListLigneEcriture() throws NotFoundException {
        EcritureComptable vEcritureComptable = comptabiliteDaoImpl.getEcritureComptable(-2);
        List<LigneEcritureComptable> listLignesBeforeFunction = vEcritureComptable.getListLigneEcriture();
        comptabiliteDaoImpl.loadListLigneEcriture(vEcritureComptable);
        List<LigneEcritureComptable> listLignesAfterFunction = vEcritureComptable.getListLigneEcriture();
        Assert.assertEquals(listLignesBeforeFunction, listLignesAfterFunction);
    }

    @Test
    public void testInsertEcritureComptable_And_testInsertListLigneEcritureComptable() throws ParseException {
        EcritureComptable vEcritureComptable = new EcritureComptable();
        JournalComptable vJournalComptable = new JournalComptable();
        vJournalComptable.setCode("AC");
        vJournalComptable.setLibelle("Achat");
        vEcritureComptable.setJournal(vJournalComptable);
        vEcritureComptable.setLibelle("Libellé de test");
        vEcritureComptable.setReference("AC-2016/00002");
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        vEcritureComptable.setDate(calendar.getTime());
        List<EcritureComptable> listInitialeEcritureComptable = comptabiliteDaoImpl.getListEcritureComptable();
        List<LigneEcritureComptable> listInitialeLigneEcritureComptable = comptabiliteDaoImpl.getListLigneEcritureComptable();
        CompteComptable vCompteComptable = comptabiliteDaoImpl.getListCompteComptable().get(0);
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(vCompteComptable, null, new BigDecimal(200), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(vCompteComptable, null, null, new BigDecimal(200)));
        comptabiliteDaoImpl.insertEcritureComptable(vEcritureComptable);
        Assert.assertEquals(listInitialeEcritureComptable.size() + 1, comptabiliteDaoImpl.getListEcritureComptable().size());
        Assert.assertEquals(listInitialeLigneEcritureComptable.size() + 2, comptabiliteDaoImpl.getListLigneEcritureComptable().size());
    }

    @Test
    public void testUpdateEcritureComptable() throws NotFoundException {
        EcritureComptable vEcritureComptable = comptabiliteDaoImpl.getEcritureComptable(-1);
        vEcritureComptable.getListLigneEcriture().get(0).setLibelle("Libellé modifié");
        comptabiliteDaoImpl.updateEcritureComptable(vEcritureComptable);
        Assert.assertEquals(true, comptabiliteDaoImpl.getEcritureComptable(vEcritureComptable.getId()).getListLigneEcriture().get(0).getLibelle().equals("Libellé modifié"));
        Assert.assertEquals(true, comptabiliteDaoImpl.getLigneEcritureComptableByEcritureIdAndLigneId(-1, 1).getLibelle().equals("Libellé modifié"));
    }

    @Test
    public void testRemoveEcritureComptable() {
        List<EcritureComptable> listInitiale = comptabiliteDaoImpl.getListEcritureComptable();
        comptabiliteDaoImpl.deleteEcritureComptable(-5);
        Assert.assertEquals(listInitiale.size() - 1, comptabiliteDaoImpl.getListEcritureComptable().size());
    }

    @Test
    public void testRemoveListLigneEcritureComptable() {
        List<LigneEcritureComptable> listInitiale = comptabiliteDaoImpl.getListLigneEcritureComptable();
        comptabiliteDaoImpl.deleteListLigneEcritureComptable(-3);
        Assert.assertEquals(listInitiale.size() - 2, comptabiliteDaoImpl.getListLigneEcritureComptable().size());
    }

    @Test
    public void testIsJournalInDatabaseFalse() {
        JournalComptable pJournalComptable = new JournalComptable();
        pJournalComptable.setCode("AD");
        pJournalComptable.setLibelle("Achat");
        Assert.assertFalse(comptabiliteDaoImpl.isJournalInDatabase(pJournalComptable));
    }

    @Test
    public void testIsJournalInDatabaseTrue() {
        JournalComptable pJournalComptable = new JournalComptable();
        pJournalComptable.setCode("AC");
        pJournalComptable.setLibelle("Achat");
        Assert.assertTrue(comptabiliteDaoImpl.isJournalInDatabase(pJournalComptable));
    }

    @Test
    public void testGetSequence_NotFound() {
        try {
            comptabiliteDaoImpl.getSequence(2016, "AD");
        } catch (NotFoundException e) {
            assert(e.getMessage().contains("La séquence n'existe pas"));
        }
    }

    @Test
    public void testGetSequence_Found() throws NotFoundException {
        SequenceEcritureComptable pSequenceEcritureComptable = comptabiliteDaoImpl.getSequence(2016, "BQ");
        Assert.assertEquals(new BigDecimal(51), new BigDecimal(pSequenceEcritureComptable.getDerniereValeur()));
    }

    @Test
    public void testInsertSequenceEcritureComptable() {
        List<SequenceEcritureComptable> listSequenceEcritureComptableInitiale = comptabiliteDaoImpl.getListSequenceEcritureComptable();

        comptabiliteDaoImpl.insertSequenceEcritureComptable(2018, "BQ");
        Assert.assertEquals(listSequenceEcritureComptableInitiale.size() + 1, comptabiliteDaoImpl.getListSequenceEcritureComptable().size());
    }

    @Test
    public void testUpdateSequenceEcritureComptable() throws NotFoundException {
        comptabiliteDaoImpl.updateSequenceEcritureComptable(2016, "AC", 100);
        Assert.assertEquals(new BigDecimal(100), new BigDecimal(comptabiliteDaoImpl.getSequence(2016, "AC").getDerniereValeur()));
    }
}