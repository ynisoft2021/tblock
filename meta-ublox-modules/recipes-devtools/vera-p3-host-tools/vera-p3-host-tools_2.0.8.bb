#
# Copyright (C) u-blox
#
# For additional copyright information see the LICENSE file in the root of this meta layer.
#

PR = "r1"
SUMMARY = "Host tools for u-blox VERA-P3"

LICENSE = "u-blox-proprietary & u-blox-open-source"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=c82f103c7edfae81aec6ae00ebb58f13 \
"

SRC_URI = " \
    ${LULA_DL_URL}u-blox-p3_v${PV}_armv7ahf-neon.tar.gz \
    file://0001-fix-build.patch \
    file://vera-p3.conf \
    file://load_vera-p3.sh \
    file://uart.json \
"

SRC_URI[md5sum] = "26b892e26e5b3a2928d980f40d913a19"
SRC_URI[sha256sum] = "940365c3ce86db3ece7232e290a6df8e0fd57a4ee2e45565ce38da87acd58ad9"

S = "${WORKDIR}/u-blox/p3-v${PV}"

P3_DIR = "${D}/opt/vera-p3"

RDEPENDS_${PN} += " \
    vera-p3-firmware \
    bash \
"

FILES_${PN} = "\
    /opt/vera-p3/vera-p3.conf \
    /opt/vera-p3/uart.json \
    /opt/vera-p3/version.txt \
    /opt/vera-p3/bin/hif \
    /opt/vera-p3/bin/hboot \
    /opt/vera-p3/bin/hif_tool \
    /opt/vera-p3/bin/load_vera-p3.sh \
    /opt/vera-p3/setup.sh \
    /opt/vera-p3/setup-common.sh \
    /opt/vera-p3/bin/armv7l \
    ${libdir}/libubxp3.so.2 \
    ${libdir}/libubxp3.so.${PV} \
    ${libdir}/libubxp3.so \
    /opt/vera-p3/scripts/* \
"

FILES_${PN}-dev = "\
    ${includedir}/libubxp3.a \
    ${includedir}/ubxp3_api.h \
    ${includedir}/ubxp3_msgs.h \
    ${includedir}/hif_msg.h \
    ${includedir}/ieee80211.h \
    ${includedir}/gos_sae_stats.h \
    ${includedir}/hif_api_connman.h \
"

ARCH = "armv7ahf"
EXTRA_OEMAKE = 'CC="${CC} ${LDFLAGS} -fgnu89-inline" LD="${LD}" AS="${AS}" M32="0" ARCH="${ARCH}"'

do_compile () {
    cd ${S}
    # Building targets in the right order according to dependencies.
    # Change this after Makefile dependencies are implemented.
    oe_runmake ubxp3
    oe_runmake ubxp3_install
    oe_runmake hif hboot
    oe_runmake install
}

do_install() {
    install -d ${P3_DIR}
    install -d ${P3_DIR}/bin
    install -m 0755 ${S}/bin/${ARCH}/hif ${P3_DIR}/bin
    install -m 0755 ${S}/bin/${ARCH}/hboot ${P3_DIR}/bin
    install -m 0755 ${S}/bin/${ARCH}/hif_tool ${P3_DIR}/bin
    install -m 0755 ${WORKDIR}/load_vera-p3.sh ${P3_DIR}/bin
    install -m 0644 ${WORKDIR}/vera-p3.conf ${P3_DIR}
    install -m 0644 ${WORKDIR}/uart.json ${P3_DIR}
    install -m 0644 ${S}/version.txt ${P3_DIR}/

    install -d ${D}${libdir}
    install -m 0755 ${S}/bin/${ARCH}/libubxp3.a ${D}${libdir}
    install -m 0755 ${S}/bin/${ARCH}/libubxp3.so ${D}${libdir}
    cp ${D}${libdir}/libubxp3.so ${D}${libdir}/libubxp3.so.${PV}
    cd ${D}${libdir}
    ln -s libubxp3.so.${PV} libubxp3.so.2

    install -d ${D}${includedir}
    install -m 0644 ${S}/src/api/include/ubxp3_api.h ${D}${includedir}/
    install -m 0644 ${S}/src/api/include/ubxp3_msgs.h ${D}${includedir}/
    install -m 0644 ${S}/src/gos/hif_msg.h ${D}${includedir}/
    install -m 0644 ${S}/src/gos/ieee80211.h ${D}${includedir}/
    install -m 0644 ${S}/src/gos/gos_sae_stats.h ${D}${includedir}/
    install -m 0644 ${S}/src/host/hif/hif_api_connman.h ${D}${includedir}/

    rm -rf ${S}/scripts/board-configs
    cp -R --no-dereference --preserve=mode,links -v ${S}/scripts/ ${P3_DIR}/scripts/
    install -m 0755 ${S}/setup.sh ${P3_DIR}/
    install -m 0755 ${S}/setup-common.sh ${P3_DIR}/
    ln -s /opt/vera-p3/bin ${P3_DIR}/bin/armv7l
}
