package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ComptabiliteManagerImplTest extends AbstractBusinessManager {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
    private DaoProxy daoProxy = Mockito.mock(DaoProxy.class, Mockito.RETURNS_DEEP_STUBS);
    private TransactionManager transactionManager = TransactionManager.getInstance();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configure(null, daoProxy, transactionManager);
    }

    @Test
    public void checkEcritureComptableUnit() throws FunctionalException, ParseException, NotFoundException {
        // Wrong and good patterns of reference
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));

        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(vEcritureComptable.getJournal())).thenReturn(true);
        Mockito.when(getDaoProxy().getComptabiliteDao().getSequence(calendar.get(Calendar.YEAR), vEcritureComptable.getJournal().getCode()).getDerniereValeur()).thenReturn(37);
        try {
            manager.addReference(vEcritureComptable);
        } catch (FunctionalException e) {
            e.printStackTrace();
        }
        try {
            manager.checkEcritureComptableUnit(vEcritureComptable);
        } catch (FunctionalException e) {
            e.printStackTrace();
        }

        vEcritureComptable.setReference("AC-2016/000040");
        try {
            manager.checkEcritureComptableUnit(vEcritureComptable);
        } catch (FunctionalException e) {
            assert(e.getMessage().contains("L'écriture comptable ne respecte pas les règles de gestion."));
        }
    }

    @Test
    public void checkEcritureComptableUnitViolation() throws FunctionalException {
        thrown.expect(FunctionalException.class);
        thrown.expectMessage("L'écriture comptable ne respecte pas les règles de gestion.");
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG2_NotEquilibree() throws FunctionalException {
        thrown.expect(FunctionalException.class);
        thrown.expectMessage("L'écriture comptable n'est pas équilibrée.");
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG2_Equilibree() throws FunctionalException {
        // Without the thrown
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(123)));
        vEcritureComptable.setReference("BQ-2016/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG3_Violated() throws FunctionalException {
        thrown.expect(FunctionalException.class);
        thrown.expectMessage("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null,
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG3_Respected() throws FunctionalException {
        // Without thrown
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(123)));
        vEcritureComptable.setReference("BQ-2016/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void testAddReferenceCodeNotRegisteredRG5() throws ParseException, NotFoundException, FunctionalException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("BQ", "Journal de la banque"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Libelle");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));

        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(pEcritureComptable.getJournal())).thenReturn(false);
        try {
            manager.addReference(pEcritureComptable);
        } catch (FunctionalException e) {
            assert(e.getMessage().contains("Le code du journal n'est pas enregistré en base de données"));
        }
    }

    @Test
    public void testAddReferenceMaxNumberRG5() throws ParseException, NotFoundException, FunctionalException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("BQ", "Journal de la banque"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Libelle");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));

        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(pEcritureComptable.getJournal())).thenReturn(true);
        Mockito.when(getDaoProxy().getComptabiliteDao().getSequence(calendar.get(Calendar.YEAR), pEcritureComptable.getJournal().getCode()).getDerniereValeur()).thenReturn(99999);
        try {
            manager.addReference(pEcritureComptable);
        } catch (FunctionalException e) {
            assert(e.getMessage().contains("Le nombre maximal d'écriture pour ce journal a été atteint"));
        }
    }

    @Test
    public void testAddReferenceSuccessRG5() throws FunctionalException, ParseException, NotFoundException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("BQ", "Journal de la banque"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Libelle");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));

        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(pEcritureComptable.getJournal())).thenReturn(true);
        Mockito.when(getDaoProxy().getComptabiliteDao().getSequence(calendar.get(Calendar.YEAR), pEcritureComptable.getJournal().getCode()).getDerniereValeur()).thenReturn(146);
        try {
            manager.addReference(pEcritureComptable);
        } catch (FunctionalException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("BQ-2016/00147", pEcritureComptable.getReference());
    }

    @Test
    public void checkEcritureComptableContext() throws ParseException, NotFoundException, FunctionalException {
        EcritureComptable pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Date date = new SimpleDateFormat("dd/M/yyyy").parse("31/12/2016");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        pEcritureComptable.setDate(calendar.getTime());
        pEcritureComptable.setLibelle("Achat");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(200),
                null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null,
                new BigDecimal(200)));
        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(pEcritureComptable.getJournal())).thenReturn(true);
        manager.addReference(pEcritureComptable);
        try {
            manager.checkEcritureComptableContext(pEcritureComptable);
        } catch (FunctionalException e) {
            assert(e.getMessage().contains("Une autre écriture comptable existe déjà avec la même référence."));
        }
    }
}
