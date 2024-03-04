//
// Created by User on 2024/3/3.
//

#include "FontFace.h"
#include "logcat.h"
#include <vector>

#define CHAR_X_OFFSET 2
#define CHAR_Y_OFFSET 2

FontFace::FontFace(FT_Byte *file_base, FT_Long file_size) {
    if (FT_Init_FreeType(&m_ft)) {
        m_errorString = "ERROR::FREETYPE: Could not init FreeType Library";
        return;
    }
    if (FT_New_Memory_Face(m_ft, file_base, file_size, 0, &m_face)) {
        m_errorString = "ERROR::FREETYPE: Failed to load font";
        return;
    }

    FT_Set_Pixel_Sizes(m_face, 0, 48);
    m_textureData = new FT_Byte[0];
}

FontFace::~FontFace() {
    FT_Done_Face(m_face);
    FT_Done_FreeType(m_ft);
    delete[] m_textureData;
}

FontFace::TextureInfo FontFace::getCharInfo(FT_ULong c) {
    if (FT_Load_Char(m_face, c, FT_LOAD_RENDER))
        LOGD("ERROR::FREETYPE: Failed to load Glyph");
    FT_Bitmap &bitmap = m_face->glyph->bitmap;
    uint newWidth = m_textureWidth + bitmap.width + CHAR_X_OFFSET;
    uint newHeight = std::max<uint>(bitmap.rows + CHAR_Y_OFFSET * 2, m_textureHeight);
    FT_Byte *newTexture = new FT_Byte [newWidth * newHeight];
    for (uint i = 0; i < newWidth * newHeight; ++i){
        newTexture[i] = 0;
    }
    for (uint i = 0; i < m_textureHeight; ++i) {
        for (uint j = 0; j < m_textureWidth; ++j) {
            newTexture[i * newWidth + j] = m_textureData[i * m_textureWidth + j];
        }
    }
    for (uint i = 0; i < bitmap.rows; ++i) {
        for (uint j = 0; j < bitmap.width; ++j) {
            newTexture[(i + CHAR_Y_OFFSET) * newWidth + j + m_textureWidth] = bitmap.buffer[i * bitmap.width + j];
        }
    }
    FontFace::TextureInfo info {
            bitmap.width,
            bitmap.rows,
            m_face->glyph->bitmap_left,
            m_face->glyph->bitmap_top,
            (int)m_face->glyph->advance.x >> 6,
            m_textureWidth
    };
    delete[] m_textureData;
    m_textureData = newTexture;
    m_textureWidth = newWidth;
    m_textureHeight = newHeight;
    return info;
}
