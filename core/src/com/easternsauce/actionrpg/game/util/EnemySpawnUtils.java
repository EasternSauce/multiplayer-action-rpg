package com.easternsauce.actionrpg.game.util;

import com.easternsauce.actionrpg.model.creature.EnemySpawn;
import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import com.easternsauce.actionrpg.model.util.Vector2;

import java.util.Arrays;
import java.util.List;

public class EnemySpawnUtils {
    public static List<EnemySpawn> area1EnemySpawns() {
        return Arrays.asList(EnemySpawn.of(Vector2.of(46.081165f, 15.265114f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(72.060196f, 31.417873f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(77.200066f, 31.255192f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(74.47733f, 25.755476f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(45.421207f, 45.40418f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(42.50976f, 42.877632f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(27.440567f, 32.387764f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(23.27239f, 31.570148f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(17.861256f, 29.470364f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(7.6982408f, 38.85155f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(7.5632095f, 51.08941f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(14.64726f, 65.53082f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(5.587089f, 64.38693f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(29.00641f, 77.44126f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(36.03629f, 75.34392f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(50.472652f, 79.4063f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(50.148594f, 73.69869f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(54.767036f, 70.07713f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(66.695274f, 70.41996f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(71.66365f, 76.8444f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(68.14547f, 84.64497f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(57.657906f, 94.204346f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(57.360214f, 106.31289f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(53.34992f, 108.87486f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(52.077705f, 114.31765f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(58.31064f, 116.29132f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(53.60553f, 122.53634f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(59.375126f, 127.002815f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(54.056587f, 132.49812f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(58.468967f, 136.74872f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(63.973305f, 141.23653f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(67.22166f, 146.12518f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(62.294132f, 149.34793f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(55.87424f, 152.88708f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(60.95999f, 156.84436f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(68.9384f, 157.29518f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(73.83359f, 159.6212f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(79.707794f, 156.41962f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(83.25423f, 151.24565f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(87.44349f, 150.14972f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(91.96663f, 147.12524f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(93.24303f, 142.64328f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(99.618805f, 138.7312f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(102.043205f, 144.3369f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(101.632095f, 150.43385f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(24.779797f, 105.06602f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(19.633848f, 107.74347f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(13.453523f, 104.866264f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(41.775314f, 145.89954f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(28.38588f, 149.543f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(5.8109603f, 157.73994f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(4.9669175f, 141.51099f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(4.5658607f, 132.52162f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(3.8313706f, 124.9922f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(3.3410232f, 121.02242f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(9.042344f, 175.50775f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(10.291371f, 185.33678f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(18.74307f, 189.86783f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(24.11259f, 183.47827f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(32.216206f, 187.60275f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(44.744465f, 176.04462f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(62.071167f, 187.91502f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(70.46168f, 193.04181f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(80.53664f, 194.43246f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(82.023094f, 185.99677f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(78.697334f, 176.75232f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(88.63461f, 170.69733f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(100.01371f, 171.65938f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(100.88453f, 180.94264f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(118.09819f, 184.26761f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(121.64451f, 142.50131f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(132.7917f, 146.49483f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(136.7625f, 159.61386f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(148.025f, 160.29198f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(152.7725f, 153.76228f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(153.67719f, 144.47525f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(148.2844f, 138.8423f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(163.85919f, 143.9521f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(169.2711f, 156.9385f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(179.0453f, 163.24689f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(185.35747f, 164.98387f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(193.81197f, 159.04521f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(194.89146f, 164.85162f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(153.76033f, 177.19379f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(144.59102f, 184.54585f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(156.58047f, 190.1377f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(170.36496f, 186.46513f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(179.10818f, 188.72041f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(184.79785f, 181.3062f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(192.6414f, 185.76282f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(190.96558f, 134.12852f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(194.79152f, 134.61769f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(192.07526f, 129.78903f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(185.60329f, 132.04916f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(146.1244f, 130.98456f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(142.62613f, 123.732834f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(142.0144f, 105.14815f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(140.0141f, 102.348625f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(140.33853f, 91.78347f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(169.81862f, 91.593094f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(175.93788f, 91.98944f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(179.8876f, 94.312645f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(181.7452f, 90.67603f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(186.8037f, 95.86144f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(189.25246f, 90.42049f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(169.01384f, 79.64462f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(156.49695f, 72.64518f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(140.31602f, 78.54003f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(152.61832f, 57.394737f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(159.92276f, 46.96312f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(183.09975f, 54.035927f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(187.63434f, 51.632175f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(184.94954f, 44.594543f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(178.90529f, 47.844643f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(177.98778f, 55.694992f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(186.96771f, 55.31115f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(193.56436f, 54.335167f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(193.12312f, 50.370644f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(184.55705f, 41.69787f), EnemyTemplate.skeleton),
                             EnemySpawn.of(Vector2.of(177.29303f, 38.892395f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(62.161907f, 4.517152f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(84.86809f, 8.742547f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(90.52131f, 11.117303f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(106.87604f, 5.119121f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(118.35929f, 3.9370656f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(126.05397f, 4.030707f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(146.1872f, 3.4932797f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(110.71863f, 20.443647f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(113.40085f, 19.460066f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(116.31426f, 18.28164f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(132.95432f, 31.945175f), EnemyTemplate.mage),
                             EnemySpawn.of(Vector2.of(142.37437f, 24.157932f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(158.12152f, 19.60879f), EnemyTemplate.archer),
                             EnemySpawn.of(Vector2.of(101.61807f, 155.82611f), EnemyTemplate.mage));
    }

    public static List<EnemySpawn> area3EnemySpawns() {
        return Arrays.asList(EnemySpawn.of(Vector2.of(33.971928f, 21.07241f), EnemyTemplate.archer)//,
                             //                             EnemySpawn.of(Vector2.of(36.3834f, 28.55842f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(49.971405f, 33.947628f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(52.668243f, 45.00484f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(43.850334f, 56.680363f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(54.796265f, 63.825066f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(66.79386f, 61.955425f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(72.42107f, 75.001045f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(60.70875f, 84.34951f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(92.49175f, 44.757214f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(103.27106f, 29.738508f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(113.615395f, 26.835396f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(108.33014f, 21.893187f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(96.46676f, 19.682264f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(87.656906f, 25.543463f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(84.562706f, 32.44827f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(91.784325f, 48.642456f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(97.16314f, 64.99985f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(96.86491f, 84.50588f), EnemyTemplate.archer),
                             //                             EnemySpawn.of(Vector2.of(35.121532f, 103.37228f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(33.87953f, 115.00769f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(44.498783f, 117.948204f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(47.33845f, 110.552605f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(56.311863f, 105.34517f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(65.79965f, 101.66881f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(76.23138f, 107.40219f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(75.437805f, 116.956726f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(68.466095f, 124.475204f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(50.446594f, 125.04712f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(38.17692f, 136.4965f), EnemyTemplate.archer),
                             //                             EnemySpawn.of(Vector2.of(41.802944f, 147.70761f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(37.937466f, 161.35631f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(54.88341f, 164.41359f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(65.077095f, 149.36624f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(77.25168f, 138.18526f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(96.61612f, 147.79411f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(98.365486f, 158.88408f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(114.42264f, 164.93347f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(93.18777f, 185.52443f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(78.01836f, 183.72467f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(134.9949f, 164.37689f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(152.91905f, 164.15259f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(162.19016f, 156.76428f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(163.29489f, 145.07349f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(165.84032f, 134.566f), EnemyTemplate.archer),
                             //                             EnemySpawn.of(Vector2.of(161.34128f, 124.161415f), EnemyTemplate
                             //                             .skeleton),
                             //                             EnemySpawn.of(Vector2.of(157.78583f, 113.20722f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(156.18457f, 102.41754f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(163.2341f, 93.55492f), EnemyTemplate.archer),
                             //                             EnemySpawn.of(Vector2.of(176.39966f, 93.62915f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(178.34964f, 83.60715f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(172.57846f, 74.36437f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(166.44185f, 66.43297f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(157.85703f, 67.71278f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(148.65504f, 76.77829f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(146.06499f, 86.56948f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(149.09619f, 95.253944f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(154.71619f, 102.9591f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(181.53673f, 164.3998f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(186.29572f, 144.02176f), EnemyTemplate
                             //                             .archer),
                             //                             EnemySpawn.of(Vector2.of(180.32324f, 140.83865f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(175.40146f, 178.00218f), EnemyTemplate.skeleton),
                             //                             EnemySpawn.of(Vector2.of(167.15323f, 182.29723f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(162.33772f, 180.4668f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(156.52663f, 177.26068f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(151.96893f, 182.01967f), EnemyTemplate.skeleton),
                             //                             EnemySpawn.of(Vector2.of(154.62996f, 186.93576f), EnemyTemplate.mage),
                             //                             EnemySpawn.of(Vector2.of(159.80215f, 187.7893f), EnemyTemplate.archer),
                             //                             EnemySpawn.of(Vector2.of(53.97292f, 96.57185f), EnemyTemplate.skeleton),
                             //                             EnemySpawn.of(Vector2.of(11.297986f, 53.920593f), EnemyTemplate.minos)

                            );
    }
}
