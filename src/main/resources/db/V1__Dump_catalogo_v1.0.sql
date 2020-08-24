-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: catalogo
-- ------------------------------------------------------
-- Server version	5.6.38

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `api_tokens`
--

DROP TABLE IF EXISTS `api_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` int(11) NOT NULL,
  `usuario_empresa` int(11) DEFAULT NULL,
  `token` longtext NOT NULL,
  `activo` tinyint(4) DEFAULT '0',
  `tipo` tinyint(4) NOT NULL DEFAULT '0',
  `ultimo_acceso` datetime DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL,
  `eliminado` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `api_tokens usuarios_idx` (`usuario`),
  CONSTRAINT `api_tokens usuarios` FOREIGN KEY (`usuario`) REFERENCES `usuarios` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_tokens`
--

LOCK TABLES `api_tokens` WRITE;
/*!40000 ALTER TABLE `api_tokens` DISABLE KEYS */;
INSERT INTO `api_tokens` VALUES (249,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjoiNzczNzAwMTA3MDAxNyIsInJvbCI6ImFkbWluX2VtcHJlc2EiLCJpYXQiOjE1Mjc2Mjk5NTIsImp0aSI6ImVPUXJYVHIxQWpIc2dYMkh5MXAybXcifQ.KmBsvOwn2sTSZeV5ZxvioVSmJrg2FI_QUDOCMZlUsKo',0,0,'2018-05-29 18:39:13','2018-05-29 18:39:13',0),(251,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjoiNzczNzAwMTIzMDAxNSIsInJvbCI6ImFkbWluX2VtcHJlc2EiLCJpYXQiOjE1Mjc4Nzg4MDYsImp0aSI6InI1VzVDb1ZiWWJJM28xWDFlMmFKRmcifQ.sy36hy_TeBBLtMcP3S1fkwabudxM-b0LBG60vMryF8Y',0,0,'2018-06-01 15:46:47','2018-06-01 15:46:47',0),(252,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjoiNzczNzAwMTIzMDAxNSIsInJvbCI6ImFkbWluX2VtcHJlc2EiLCJpYXQiOjE1Mjc4Nzk2OTYsImp0aSI6IkxHVmpOUjB5TXJwUmxIbTRGOGxwWlEifQ.kHLq7Io1sjiKLNx-8PxIjEgpEfu7aIo-I-lWwp7_w-Y',0,0,'2018-06-01 16:01:37','2018-06-01 16:01:37',0),(259,2,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIyIiwidXN1YXJpbyI6InNhbnRpYWdvQGJpbGxlci51eSIsImVtcHJlc2EiOm51bGwsImlhdCI6MTUyNzg4MTM1MSwianRpIjoiSV9GQVE4dW1IUTU5ODlZSjFZRWFBQSJ9.Vi0GJ8CJz29vXjsFvS7NnuYw4KIgEbtGVe_2piSN9Ao',0,0,'2018-06-01 16:29:11','2018-06-01 16:29:11',0),(260,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjpudWxsLCJpYXQiOjE1Mjc4ODQ5NTYsImp0aSI6IlJOUGJKNm1xcTdXcUdYQWFWcThyN2cifQ.4b2lMwwpzihu6bqMbljtte6v6AA6Ypsaa4gr0nv2rMs',0,0,'2018-06-01 17:29:16','2018-06-01 17:29:16',0),(261,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjpudWxsLCJpYXQiOjE1Mjc4ODUxOTYsImp0aSI6IlZqVG0tRGpZU2F3emFGOWlnMFJNMFEifQ.D0SLToWheu287PzHKBcXba4xxNQZBGd_Xa3U8hLR-Cc',0,0,'2018-06-01 17:33:17','2018-06-01 17:33:17',0),(262,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXN1YXJpbyI6Im1heEBiaWxsZXIudXkiLCJlbXByZXNhIjpudWxsLCJpYXQiOjE1Mjc4ODUzMDEsImp0aSI6IllzQnpXRlJWUUdJWDYtdm9jT25iaHcifQ.PVLu5a9lc1lh4TYel1nHLlQpipZ-BK4eou_Tif7XpIc',0,0,'2018-06-01 17:35:02','2018-06-01 17:35:02',0),(264,2,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWQiOiIyIiwidXN1YXJpbyI6InNhbnRpYWdvQGJpbGxlci51eSIsImVtcHJlc2EiOiI3NzM3MDAxMDcwMDE5Iiwicm9sIjoiYWRtaW5fZW1wcmVzYSIsImlhdCI6MTUyODEyMjE5OSwianRpIjoiV3ZaamRWSncyM1ZQYTNIWUJXME9XUSJ9.4ruJumjdU83LIQW4rGHMuD8-sI8n3aLIb9CFR',0,0,'2018-06-01 18:36:02','2018-06-01 18:36:02',0),(265,1,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjEiLCJ1c3VhcmlvIjoibWF4QGJpbGxlci51eSIsImVtcHJlc2EiOm51bGwsImlhdCI6MTUyODEyNDg2MCwianRpIjoiZkxndUVJZ1NUTTRiWHpqaHp4R2Z3ZyJ9.xNGjbDevYJQ-UP8z4NNzreKqFV7mKA3NCjN_7UOTwnY',0,0,'2018-06-04 12:07:41','2018-06-04 12:07:41',0),(266,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..b21k8pZRRloHE_ySZZ-N_w.IReTsgeaQoXy9y5gQBEFvb7S-CqF6YnDB0UFqlywYRYgFlXU2KXEzNT2ZkOc5M3UUC-dMzOkbUq4WCF7i9ILC6LsNbdw5erNMZ3CF0BCods422DnGPK0GWTz4QVyCGrfGpmD2t8_TJT1pt-malizvPiSrfu26-rLsVEn0gvvcpQGgn3SceO1P9ylWQ25TqxdqBYiVnwNmiLVXyhkhGrlzvRWeWW2Ad3X5L8nq668hHwjr5HlgRYvmmS_oArXr33ZDjEuXRGhw3pTr1TSpT33lJVN6A9EE9EkjXTjJYvOxNw.RS0h5OoP9CPaRYoMHTLtkg',0,0,'2018-06-06 10:35:03','2018-06-06 10:35:03',0),(267,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..LLdbvVoMgR9FKXPDevcHtA.buEa9Gq8Re1iMhF4Sp4-Jd-bkUGKkVXYlQclaaqa1sLX5uSa6IM8F60Kn0YPgpoQE1AyxkKT-CkdzEzePk6SUqUedKpF-z91sGR5j_ba4qappVU41i8su5rKCEH7iHeQm161HMcO6qzi9azCqD7ry_nnfTPqbx74Pk_Seyte2g2PRbkpYu3_zMUoZZsnu_-EmZqAyvQVvJI3wdcBPMqVlogA5JazIiwWdSNHdRaMQbzR-fB3Pv06IgI0SfXxwwYBoOKUzjxoi-VmERZYI6K0jiPRTc9zCDleM25yrn2pHx8.wdOtqawBkJRBq5XxYSTONA',0,0,'2018-06-06 10:35:53','2018-06-06 10:35:53',0),(268,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..uwcGF96TgT5ad7CI_eN8gw.E-zHOuVV0nL8erGNK_FxOeX9ad1uvpuU0I2XzLTCSD99Y5Kk_1Q8WvVbS6F6-VFk_yhffnR3mEE2u3SCSToACuI6yjsak1QbFENHdG7wEaXFzHIeT_ElMqXvP8mKQv-1EilwnBo4CAtU1ShOgVPWeeveuZAB1n5ROWwusymlrMgBFNxB0gD9tJZonPPwbIY9yQ0EC8v6aKbDi-9UKQmZCPuRVxU4kqmOytZ0xnF5_eDXnUBF-3kpn-zVMz1VoX1dydOCt6iYDt-cvMH978rSLRhSo53-K_0A7Hlw6Phx2Zk.AKMdGyqKsbPY6-_lFF2cbg',0,0,'2018-06-06 10:42:03','2018-06-06 10:42:03',0),(269,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..UUu-7AEyUEnNV8rXtwG18Q.0vs64gKLGKLITeSZqZoeNloDKLCxOZAANXZDjbpVileGU05tIuS2ZR4Iux0LInKpcx4E2OcPED5UiLAn74BxRVfaoUzU_QoFr653shHXPrezy7hDuM7rDcYJKqyFBu60ssES_nhyZ8ygPNF23AmaHeKoGqSYeKkb9WanAFt0ZV-vlXLyBCcM9Mwql11miGlm6BJu1jboLk7Q7DeellQ9DPOC9Tr1uKgDHHP42WIc8f4E1p2xaIyphV6Li-aOV850efoYAeA6LW0HiqSfZcq2E-fpqO0cFgMxeUmlhy0Unxk.A4eK_n2D2-F4L6P5_tnUrQ',0,0,'2018-06-06 11:09:40','2018-06-06 11:09:40',0),(270,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..v1j2JiERq4tZ_iatFjUuOA.qFjM-8Gy-B_RLs0tY3BPeXyXZXSix6igPLREfrPty96vZAWPuNYIEkY3HyH_9SVxwbJwiLxRztJv6v8LzepG1ul6_1iSmjekuL6rH5u1Aw0tGGWoXGkLxdVJiopx4jR6Tgjk6LcAszPruBbpdAp8q54W2my4-2J8Mhbdc2SIVooRKa33tbzUDo6xvHLReAdh1UlxAYaI8SsxeB77fTw-ssK0gjV4CgxJd-1yuVaf_61gMc2BtuCV8v9G56nvbeYJheQ9NWE_1OfTeBjoTL_gS-P5SgjoUYDkoMl4yKu4lXk.hpklAbFECp1SmwGgFQZmiw',0,0,'2018-06-06 11:14:13','2018-06-06 11:14:13',0),(271,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..lSXNaU0s4IsEQm-BesMcMw.46TRr2-SD8JDeQsuXPpKv2mt8HlfF0EiLQsDmSD4NRnmE7oSkHtARmvm0BdEL-wFRcEUauq_Pp96wHO3xJb7PQa3W4fAUfLRKLO7Siryn-iCI3JyuHYlIk5tXklrAsasrJ2sU7AWcy5XaDnwKUq1MMBNPYTXr-S4-hEexZLNhrb7M02Z1L_nxplCcvtJ0HbzE5kxSAqWX_NSWpm8gQLPR1yAgTiqolH9y9gdwqA378bxH-DrkADK0ijVSXpzXTD5gfaOr8AbhIb_Rw_iOcPDRyjQgBkci6F4w7IHxlEC6Ow.MqNxZmI6voXFqml6KmLldg',0,0,'2018-06-06 11:57:03','2018-06-06 11:57:03',0),(272,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..b3WieziFAhvNhm3lt4xH7A.oEmpX_2AioDPT-OPhb-ylXeYxw_0WbF1B_bBOh932NMj3moutueXrny4wKWvQZWLkQsq8ZiRTv23OwFzTfzWXodaqblNj88pPySvPPWlnOGJnWYbgJ206j8xkPL4Lck3gTFjq8SZuohrPhqDw-reQfHV5IUBiEno1WY2UCJfnaqAplwP3JuA6e9QdSHGd2rGRoDThyLe_8YskKEmWmxqd5NwDZizTzGZxSNHBioct5iFuPVou7B25hgF4mNbUiuH17DR7HKxYTNIeCJJVvz2nSRKqgFkQz85LoAuWzWxBFc.wOsnGLA1NQF2JJUV-bqtnA',0,0,'2018-06-06 12:03:27','2018-06-06 12:03:27',0),(273,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..Nji3vEvIXOz31Qm944FHIw.Rkw3FKUK9iOFQPzAWc4OwLoGiISg3ZGxuofszdaPvPhu-YoLfzEUKfxAQYAWuea1VVdJab5Wn69qZI4Y8Kox811zUigGk11kOmEvLBMwScYlWNzZ1YFsp1JEP75JKguqnDLhR6OnE2HQ6B-FWHDHLbyWKHeGLkLAtST7r45MuBZzXq-JPF58sCEhP2iejzqKBnw93TSYJLUG64GdsYObxwI7ig1BAaP4TZpx7ImONr_SCr58yOpkcksQm5NJLxs2Qn_7V4GAu-wO1n3HbUdWPA.UC5-ibfu2l-Kd0p6Vz_faA',0,0,'2018-06-06 12:28:30','2018-06-06 12:28:30',0),(274,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..6NVFSh2P9I2ALxw5LL9Rxw.SzKP2-q0JhzJ6AEs-ngSb6BuX_wKj7ISut86LRZKW-iljRPYkc49prxW2hLQT1GN5NPifnY0bvtWm474xelihyoxmL1hDGtbWb3rNYd65nruSi_OOQipGw-2_fQpFbh_QLhOlAtY1VetvZDs_pxoYR_vLCj4ELGFW5EekIU53-oiQ-sJF5sEgCcoPmPfLW3Q9smIK0XWnitb5ra9TfxD745bOoTcr5RbE6WEi_OQ49Dh7JH99Cf1yuMp_bD-aVmCpxwv0niE-sX8tOD7DnXxVtCBqJ8ezMQTPgMhQylhKulElXlImEcgFMR-nRlCOnPO.8HhhYWTQccNGd2JAT9QFvQ',0,0,'2018-06-06 12:50:48','2018-06-06 12:50:48',0),(275,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..sL_yjX6Vh5an02Jnm2E-Ig.o0pLztMTK0fYumpSIVe8yelZNIWPdfv1FGBJ3ed-l7LQAO7rO81gt_bNhEE1dPEYR1QBJyCywYVk2JWFpknp6X9n3mNFBNZ1egSRAGSNCdiRxlhZzU2xm3HCPU0w_tzhNtAbcpYzv8qbOFKsqXN_jGkHvPFJvPoOwcOfuhyyhPe1_3pU0qN9At7vyqR1y-jl76Q5jFJx7DQuZe5h_JWUQ9CfxBuSZBUJ8eYA5rGvv-EwiFWUYEuZQmFLZ9gZf5TdaGod5nNDH1sN1U9RZcMbP_msdI5jgg7H6nSSgN29A4w.Igs0bbolE0WHITPG0elrWw',0,0,'2018-06-06 15:26:37','2018-06-06 15:26:37',0),(276,1,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..txqW3luG7jug_nq0U7Rm_Q.uzmNwawvEA2DBkA4ujkN7Fx8UfTUtFbxhT-i28GbyQQoS3FZosKCqprimiAYm6epbOEiL1v50eXV-8U5F9POrNVQ5ofHlICrZMuWB53ogylpdgijVFyOlwMyKzurHggMQZRgVo6jqPJIw1ENWhUXYh4h25uKdMx3O7Fk_Gca1RObiccaMsnfh2z2Gwtziazq03rTVfgqJmUo4i__9RlY3uUA2HHDi1opYoWqpJBWzGVQQ5cpmzcV8NHRVTS3c3ZZXNnoMb3iP6_2zO5UelZciA.7xdIRflGApH_up2qWnJtDw',0,0,'2018-06-06 15:53:49','2018-06-06 15:53:49',0),(277,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..vb4g7kE93huJ7IJxaqOuog.UzmiECbqvGwxLLjh40h9qbG_-UB6LkOt5hohl5AldHUVbjqPmLyYcQB_VdkxBqFFTTjTB4J_69FLdBJd_1UUJckjTSNbKtiq8ozjpzvBSaRYtECyqrhQ5uOdZ33dGejRWz-R2ejomP86m7QNfTnXJgIUcirnKQZzRS6oQtw6KVhDke40HDSnl599lgLJjAiZuVzAQb_IqE2M_jHXVPoLeqJOkU6TJk8BzybmDVcSmYQsIIcrsmqUI5NXSVmPkDIbSRCUcRqg95CdqcyUNeVi-R2UvfzFIA3XagRfIE8my_Y.9fc1AUAK0shn4D7iWGwhFA',0,0,'2018-06-06 16:52:31','2018-06-06 16:52:31',0),(278,1,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..-wkaKoXSYgg4tQsDAJKzhg.QwJNy7WDdTWvSidytlB23-IgvLOzYO9NWESN4qvyakwuVc-LzkHMl4b-vGMn-9nFrva1ywnmKfO1onPtumtKLGKCHTlD1g5YEMRidgUDqLVl9b9MSgZHpeff5LWholViyVWO3wuWl8x27Laiti-VTbHG7lbvqb28HFE69-eRvseh5NMqwtZ7_uAL5XsfeNR5mmNAMjiMsD8hmXkECLASdYwAPQBCuLx-rDYMSXPgPyFjZaljVCzUrvxP33oKp2wIgGwshJ3frOH_T9bCfPrglw.WcSV8AYybeEqi8Uo2sWPRQ',1,0,'2018-06-07 12:09:57','2018-06-07 12:09:57',0),(279,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..cmziQ7gqQZn_dt7Hpg4mPg._Qsw-eWsaACFkLVrYjxc3PK2JTR9cQj8aMhtBsKQZ0p60Ufwvc2101LBDjdfZrX9IfAkBgKGHIxQQDMz7C5246HNevgcbu2ghilVmcGJvRFQRWOb5fxECsAdyj8mtWrWgMPbvcDruLFBdMJPchM0ow-RxGq91rM1EuXs3MxbAqRQkG75YI8s1flOMjkrjEdRrhhFBEFfq6nfw_3mJAYXQ1pxjoeeRf6p3obfLloUVV7may587DQ2IeaM2ljLIrQC9XczvxSlBPaqbSFzy4YY4LSrSu94PdhKcDO09sYjRxM._ocuYKVbHH1Ql4gM_K6NeA',0,0,'2018-06-07 12:15:37','2018-06-07 12:15:37',0),(280,2,NULL,'eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiY3R5IjoiSldUIn0..ryAG1C3AC2h_OsV4iDirdw.tNz_NvUUHeAYq0XRnjnagDaA40l-40zoVoisRxs4HA-p9LbR9s1x76zFHTjU2WQdwjyRhiY35xomeH51AJ_2I9qsw4EI_vU1j1a6jssHBlsW7ycE2DmH7qwbvHMmuwpLuSqGSn9sfWfFpMTYKbsZEgTWhfsYS-6o-wjt0UAw-YtUh30aIJFxnH30wkVb8PyUXFScKbk1fSDWMSkRlyZ0sO166TejUukP-JUsin3jz_hGvMg6M58P8_2yI0JfvrUGqQKSdDh7MpxQ_nK31YIyemvetOaA5jpx3HZr6BTVqjQ.Bnkev2f-GMKAT2tKJlMZOg',1,0,'2018-06-07 12:15:40','2018-06-07 12:15:40',0);
/*!40000 ALTER TABLE `api_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categorias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publicador` int(11) NOT NULL,
  `nivel` int(11) NOT NULL,
  `nombre` varchar(70) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `posicion` int(11) DEFAULT NULL,
  `padre` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `categorias empresas_idx` (`publicador`),
  KEY `categorias padre_idx` (`padre`),
  CONSTRAINT `categorias empresas` FOREIGN KEY (`publicador`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `categorias padre` FOREIGN KEY (`padre`) REFERENCES `categorias` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,1,2,'Hair care','Cuidado del cabello',1,NULL),(2,1,2,'Home care','Cuidado del hogar',2,NULL),(3,1,1,'Shampoo','Shampoo',1,1),(4,1,1,'Detergente Liquido para la Ropa','Detergente Liquido para la Ropa',2,2);
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empaques`
--

DROP TABLE IF EXISTS `empaques`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empaques` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publicador` int(11) NOT NULL,
  `gtin` varchar(45) NOT NULL,
  `nivel` int(11) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `cantidad` decimal(18,4) DEFAULT NULL,
  `presentacion` varchar(3) DEFAULT NULL,
  `contenido_neto` decimal(18,4) DEFAULT NULL,
  `peso_bruto` varchar(45) DEFAULT NULL,
  `unidad_medida` varchar(3) DEFAULT NULL,
  `alto` decimal(18,4) DEFAULT NULL,
  `ancho` decimal(18,4) DEFAULT NULL,
  `profundidad` decimal(18,4) DEFAULT NULL,
  `cantidad_minima` decimal(18,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `empaques empresas_idx` (`publicador`),
  KEY `empaques presentaciones_idx` (`presentacion`),
  CONSTRAINT `empaques empresas` FOREIGN KEY (`publicador`) REFERENCES `empresas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `empaques presentaciones` FOREIGN KEY (`presentacion`) REFERENCES `presentaciones` (`nombre`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empaques`
--

LOCK TABLES `empaques` WRITE;
/*!40000 ALTER TABLE `empaques` DISABLE KEYS */;
INSERT INTO `empaques` VALUES (1,1,'934232234710',1,'Pack',4.0000,'PK',800.0000,'800','ml',30.0000,30.0000,30.0000,NULL),(2,1,'23446543233',1,'Pack',12.0000,'PK',800.0000,'800','ml',30.0000,30.0000,30.0000,NULL),(3,1,'93423223122',1,'Pack',24.0000,'PK',800.0000,'800','ml',30.0000,30.0000,30.0000,NULL);
/*!40000 ALTER TABLE `empaques` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empresas`
--

DROP TABLE IF EXISTS `empresas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empresas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gln` varchar(45) NOT NULL,
  `razon_social` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `rut` varchar(14) NOT NULL,
  `activo` tinyint(4) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  `eliminado` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empresas`
--

LOCK TABLES `empresas` WRITE;
/*!40000 ALTER TABLE `empresas` DISABLE KEYS */;
INSERT INTO `empresas` VALUES (1,'7737001230015','Arcos Plateados','Burger Queen','214372420012',1,'2018-04-30 00:00:00','2018-04-30 00:00:00',0),(2,'7737001070017','Chokora SRL','Biller','214372420012',1,'2018-05-02 00:00:00','2018-05-02 00:00:00',0),(3,'7737001070019','Felipe Stanham','Ing. Felipe Stanham','216993640019',1,'2018-05-02 00:00:00','2018-05-02 00:00:00',0);
/*!40000 ALTER TABLE `empresas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grupos`
--

DROP TABLE IF EXISTS `grupos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grupos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_publicador` int(11) NOT NULL,
  `nombre` varchar(70) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `grupos empresas _idx` (`id_publicador`),
  CONSTRAINT `grupos empresas ` FOREIGN KEY (`id_publicador`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grupos`
--

LOCK TABLES `grupos` WRITE;
/*!40000 ALTER TABLE `grupos` DISABLE KEYS */;
/*!40000 ALTER TABLE `grupos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grupos_empresas`
--

DROP TABLE IF EXISTS `grupos_empresas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grupos_empresas` (
  `grupo_id` int(11) NOT NULL,
  `empresa_id` int(11) NOT NULL,
  PRIMARY KEY (`grupo_id`,`empresa_id`),
  KEY `grupos_empresas empresas_idx` (`empresa_id`),
  CONSTRAINT `grupos_empresas empresas` FOREIGN KEY (`empresa_id`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `grupos_empresas grupos` FOREIGN KEY (`grupo_id`) REFERENCES `grupos` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grupos_empresas`
--

LOCK TABLES `grupos_empresas` WRITE;
/*!40000 ALTER TABLE `grupos_empresas` DISABLE KEYS */;
/*!40000 ALTER TABLE `grupos_empresas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `params`
--

DROP TABLE IF EXISTS `params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `params` (
  `nombre` varchar(255) NOT NULL,
  `valor` longtext NOT NULL,
  PRIMARY KEY (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `params`
--

LOCK TABLES `params` WRITE;
/*!40000 ALTER TABLE `params` DISABLE KEYS */;
INSERT INTO `params` VALUES ('JWT_SECRET_KEY','Xn2r5u8x/A?D*G-KaPdSgVkYp3s6v9y$'),('TOKEN_EXP_TIME','1440'),('email','matigru@gmail.com'),('password','Size4566');

/*!40000 ALTER TABLE `params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `presentaciones`
--

DROP TABLE IF EXISTS `presentaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `presentaciones` (
  `nombre` varchar(45) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `presentaciones`
--

LOCK TABLES `presentaciones` WRITE;
/*!40000 ALTER TABLE `presentaciones` DISABLE KEYS */;
INSERT INTO `presentaciones` VALUES ('AE','Aerosol'),('BA','Barril'),('BG','Bolsa'),('BME','Blister'),('BR','Barra'),('BX','Caja'),('CBL','Botella'),('CR','Casillero'),('CS','Caja'),('CT','Caja de cartón'),('CX','Lata'),('DPE','Display'),('DR','Tambor'),('DS','Display'),('JR','Pote'),('MPE','Multipack'),('NE','Sin empaque'),('PK','Pack'),('PL','Pallet'),('PU','Bandeja'),('SA','Funda'),('SH','Sachet'),('STL','Stick'),('TU','Tubo'),('X1','Pallet'),('X8','Dispensador');
/*!40000 ALTER TABLE `presentaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `publicador` int(11) NOT NULL,
  `cpp` varchar(20) DEFAULT NULL,
  `gtin` varchar(20) DEFAULT NULL,
  `descripcion` varchar(255) NOT NULL,
  `marca` varchar(70) NOT NULL,
  `categoria` int(11) DEFAULT NULL,
  `presentacion` varchar(3) NOT NULL,
  `contenido_neto` decimal(18,4) NOT NULL,
  `unidad_medida` varchar(3) NOT NULL,
  `pais_origen` varchar(2) DEFAULT NULL,
  `foto` int(11) DEFAULT NULL,
  `alto` decimal(18,4) DEFAULT NULL,
  `ancho` decimal(18,4) DEFAULT NULL,
  `profundidad` decimal(18,4) DEFAULT NULL,
  `peso_bruto` decimal(18,4) DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  `eliminado` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cpp_UNIQUE` (`cpp`),
  UNIQUE KEY `gtin_UNIQUE` (`gtin`),
  KEY `productos categorias principal_idx` (`categoria`),
  KEY `productos empresas_idx` (`publicador`),
  KEY `productos presentaciones_idx` (`presentacion`),
  CONSTRAINT `productos categorias principal` FOREIGN KEY (`categoria`) REFERENCES `categorias` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `productos empresas` FOREIGN KEY (`publicador`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `productos presentaciones` FOREIGN KEY (`presentacion`) REFERENCES `presentaciones` (`nombre`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `productos publicador` FOREIGN KEY (`publicador`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,1,NULL,'17501001169098','Shampoo 2en1 C. Clásico','Pantene',3,'BX',200.0000,'ml','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(3,1,NULL,'7501001169091','Shampoo Rizos Definidos','Pantene',3,'BX',200.0000,'ml','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(4,1,NULL,'7506339355123','Acondicionador Summer','Pantene',3,'BX',200.0000,'ml','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(5,1,NULL,'7501001171780','Acondicionador Liso Extremo','Pantene',3,'SH',10.0000,'ml','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(7,1,NULL,'17791290011561','Matic Detergente Líquido 1er lavado','Nevex',4,'BX',3.0000,'l','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(8,1,NULL,'17791290011554','Matic Detergente Líquido 1er lavado','Nevex',4,'CBL',3.0000,'l','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0),(9,1,NULL,'75010011690934','Shampoo Rizos Definidos','Pantene',3,'BX',200.0000,'ml','MX',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00','0000-00-00 00:00:00',0);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos_empaques`
--

DROP TABLE IF EXISTS `productos_empaques`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productos_empaques` (
  `producto` int(11) NOT NULL,
  `empaque` int(11) NOT NULL,
  PRIMARY KEY (`producto`,`empaque`),
  KEY `productos_empaques empaques_idx` (`empaque`),
  CONSTRAINT `productos_empaques empaques` FOREIGN KEY (`empaque`) REFERENCES `empaques` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `productos_empaques productos` FOREIGN KEY (`producto`) REFERENCES `productos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos_empaques`
--

LOCK TABLES `productos_empaques` WRITE;
/*!40000 ALTER TABLE `productos_empaques` DISABLE KEYS */;
INSERT INTO `productos_empaques` VALUES (1,1),(3,1),(7,1),(4,2),(8,2),(5,3),(9,3);
/*!40000 ALTER TABLE `productos_empaques` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos_excel`
--

DROP TABLE IF EXISTS `productos_excel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productos_excel` (
  `empresa` int(11) NOT NULL,
  `cpp` varchar(45) DEFAULT NULL,
  `gtin` varchar(45) DEFAULT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  `marca` varchar(45) DEFAULT NULL,
  `categoria` varchar(45) DEFAULT NULL,
  `presentacion` varchar(45) DEFAULT NULL,
  `contenido_neto` varchar(45) DEFAULT NULL,
  `unidad_medida` varchar(45) DEFAULT NULL,
  `pais_origen` varchar(45) DEFAULT NULL,
  `foto` varchar(45) DEFAULT NULL,
  `alto` varchar(45) DEFAULT NULL,
  `ancho` varchar(45) DEFAULT NULL,
  `profundidad` varchar(45) DEFAULT NULL,
  `peso_bruto` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos_excel`
--

LOCK TABLES `productos_excel` WRITE;
/*!40000 ALTER TABLE `productos_excel` DISABLE KEYS */;
/*!40000 ALTER TABLE `productos_excel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rbac_permisos`
--

DROP TABLE IF EXISTS `rbac_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rbac_permisos` (
  `permiso` varchar(255) NOT NULL,
  PRIMARY KEY (`permiso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rbac_permisos`
--

LOCK TABLES `rbac_permisos` WRITE;
/*!40000 ALTER TABLE `rbac_permisos` DISABLE KEYS */;
INSERT INTO `rbac_permisos` VALUES ('obtener-productos');
/*!40000 ALTER TABLE `rbac_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rbac_roles`
--

DROP TABLE IF EXISTS `rbac_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rbac_roles` (
  `rol` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  `eliminado` varchar(45) NOT NULL DEFAULT '0',
  PRIMARY KEY (`rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rbac_roles`
--

LOCK TABLES `rbac_roles` WRITE;
/*!40000 ALTER TABLE `rbac_roles` DISABLE KEYS */;
INSERT INTO `rbac_roles` VALUES ('admin_empresa','Administrador de Empresa','2018-05-09 00:00:00','2018-05-09 00:00:00','0'),('admin_sistema','Administrador de Sistema','2017-10-10 00:00:00','2017-10-10 00:00:00','0'),('minorista','Minorista','2018-04-30 00:00:00','2018-04-30 00:00:00','0');
/*!40000 ALTER TABLE `rbac_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rbac_roles_permisos`
--

DROP TABLE IF EXISTS `rbac_roles_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rbac_roles_permisos` (
  `rol` varchar(255) NOT NULL,
  `permiso` varchar(255) NOT NULL,
  PRIMARY KEY (`rol`,`permiso`),
  KEY `roles_premisos permisos_idx` (`permiso`),
  CONSTRAINT `roles_premisos permisos` FOREIGN KEY (`permiso`) REFERENCES `rbac_permisos` (`permiso`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `roles_premisos roles` FOREIGN KEY (`rol`) REFERENCES `rbac_roles` (`rol`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rbac_roles_permisos`
--

LOCK TABLES `rbac_roles_permisos` WRITE;
/*!40000 ALTER TABLE `rbac_roles_permisos` DISABLE KEYS */;
INSERT INTO `rbac_roles_permisos` VALUES ('minorista','obtener-productos');
/*!40000 ALTER TABLE `rbac_roles_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `usuario` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `reseteo_contrasena` tinyint(4) NOT NULL DEFAULT '0',
  `validado` tinyint(4) NOT NULL DEFAULT '0',
  `activo` tinyint(4) NOT NULL DEFAULT '0',
  `eliminado` varchar(45) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Max Alegre','max@biller.uy','max@biller.uy','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',0,1,1,'0','2018-05-05 00:00:00','2018-05-05 00:00:00'),(2,'Santiago Israel','santiago@biller.uy','santiago@biller.uy','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',0,1,1,'0','2018-05-05 00:00:00','2018-05-05 00:00:00');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios_empresas`
--

DROP TABLE IF EXISTS `usuarios_empresas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios_empresas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` int(11) NOT NULL,
  `empresa` int(11) NOT NULL,
  `rol` varchar(255) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  `eliminado` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuarios_empresas usuarios empresas roles` (`usuario`,`empresa`,`eliminado`),
  KEY `usuarios_empresas usuarios_idx` (`usuario`),
  KEY `usuarios_empresas empresas_idx` (`empresa`),
  KEY `usuarios_empresas roles_idx` (`rol`),
  CONSTRAINT `usuarios_empresas empresas` FOREIGN KEY (`empresa`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `usuarios_empresas roles` FOREIGN KEY (`rol`) REFERENCES `rbac_roles` (`rol`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `usuarios_empresas usuarios` FOREIGN KEY (`usuario`) REFERENCES `usuarios` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios_empresas`
--

LOCK TABLES `usuarios_empresas` WRITE;
/*!40000 ALTER TABLE `usuarios_empresas` DISABLE KEYS */;
INSERT INTO `usuarios_empresas` VALUES (1,1,1,'admin_empresa','2018-05-22 00:00:00','2018-05-22 00:00:00',0),(2,1,2,'admin_empresa','2018-05-22 00:00:00','2018-05-22 00:00:00',0),(3,2,3,'admin_empresa','2018-05-31 00:00:00','2018-05-31 00:00:00',0);
/*!40000 ALTER TABLE `usuarios_empresas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visible_por`
--

DROP TABLE IF EXISTS `visible_por`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visible_por` (
  `empresa` int(11) NOT NULL,
  `grupo` int(11) NOT NULL,
  `producto` int(11) NOT NULL,
  `categoria` int(11) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_edicion` datetime NOT NULL,
  `eliminado` tinyint(4) NOT NULL,
  KEY `visible_por empresas_idx` (`empresa`),
  KEY `visible_por productos_idx` (`producto`),
  CONSTRAINT `visible_por empresas` FOREIGN KEY (`empresa`) REFERENCES `empresas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `visible_por productos` FOREIGN KEY (`producto`) REFERENCES `productos` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visible_por`
--

LOCK TABLES `visible_por` WRITE;
/*!40000 ALTER TABLE `visible_por` DISABLE KEYS */;
INSERT INTO `visible_por` VALUES (1,0,1,0,'2018-05-22 00:00:00','2018-05-22 00:00:00',0),(1,1,3,1,'2018-05-22 00:00:00','2018-05-22 00:00:00',0);
/*!40000 ALTER TABLE `visible_por` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-19 15:05:35