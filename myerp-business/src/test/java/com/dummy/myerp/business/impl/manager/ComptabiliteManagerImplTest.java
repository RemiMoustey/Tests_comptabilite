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
import org.junit.Test;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configure(null, daoProxy, transactionManager);
    }

    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void addReference() throws ParseException, NotFoundException {
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

        Mockito.when(getDaoProxy().getComptabiliteDao().isJournalInDatabase(pEcritureComptable.getJournal())).thenReturn(true);

        Mockito.when(getDaoProxy().getComptabiliteDao().getSequence(calendar.get(Calendar.YEAR), pEcritureComptable.getJournal().getCode()).getDerniereValeur()).thenReturn(99999);
        try {
            manager.addReference(pEcritureComptable);
        } catch (FunctionalException e) {
            assert(e.getMessage().contains("Le nombre maximal d'écriture pour ce journal a été atteint"));
        }

        Mockito.when(getDaoProxy().getComptabiliteDao().getSequence(calendar.get(Calendar.YEAR), pEcritureComptable.getJournal().getCode()).getDerniereValeur()).thenReturn(146);
        try {
            manager.addReference(pEcritureComptable);
        } catch (FunctionalException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("BQ-2016/00147", pEcritureComptable.getReference());
    }
}
