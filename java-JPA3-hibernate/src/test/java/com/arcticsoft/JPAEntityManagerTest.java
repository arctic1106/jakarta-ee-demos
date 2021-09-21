package com.arcticsoft;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceConfiguration;

public class JPAEntityManagerTest {
	private static EntityManager em;
	private static List<Album> albumList = new ArrayList<>();

	@BeforeAll
	static void setUp() {
		em = new PersistenceConfiguration("persistenceunit")
				.managedClass(Album.class)
				.property("jakarta.persistence.jdbc.driver", "org.h2.Driver")
				.property("jakarta.persistence.jdbc.url", "jdbc:h2:~/h2db")
				.property("jakarta.persistence.jdbc.user", "sa")
				.property("jakarta.persistence.jdbc.password", "")
				.createEntityManagerFactory()
				.createEntityManager();

		em.getTransaction().begin();

		var album1 = new Album("KIDS SEE GHOSTS", LocalDate.of(1999, Month.AUGUST, 19));
		em.persist(album1);
		albumList.add(album1);

		var album2 = new Album("A Love Supreme", LocalDate.of(1965, Month.FEBRUARY, 1));
		em.persist(album2);
		albumList.add(album2);
	}

	@Test
	public void insertAlbum() {
		var album3 = new Album("Album 3", LocalDate.now());
		em.persist(album3);
		albumList.add(album3);
		assertTrue(albumList.contains(album3));
	}

	@Test
	public void updateAlbum() {
		var album = albumList.get(0);
		album.setTitle("Updated Title");
		em.merge(album);
		var updatedAlbum = em.find(Album.class, album.getId());
		assertEquals("Updated Title", updatedAlbum.getTitle());
	}

	@Test
	public void deleteAlbum() {
		var album = albumList.get(1);
		em.remove(album);
		var deletedAlbum = em.find(Album.class, album.getId());
		assertNull(deletedAlbum);
	}

	@Test
	public void retrieveAlbumById() {
		var album = albumList.get(0);
		var retrievedAlbum = em.find(Album.class, album.getId());
		assertNotNull(retrievedAlbum);
		assertEquals(album.getTitle(), retrievedAlbum.getTitle());
	}
}